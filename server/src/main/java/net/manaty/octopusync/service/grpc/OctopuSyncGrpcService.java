package net.manaty.octopusync.service.grpc;

import io.grpc.Status;
import io.reactivex.Completable;
import io.vertx.core.Future;
import io.vertx.grpc.GrpcBidiExchange;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.api.*;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.service.EventListener;
import net.manaty.octopusync.service.client.ClientTimeSynchronizer;
import net.manaty.octopusync.service.db.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class OctopuSyncGrpcService extends OctopuSyncGrpc.OctopuSyncVertxImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OctopuSyncGrpcService.class);

    private final Vertx vertx;
    private final Storage storage;
    private final Set<EventListener> eventListeners;
    private final Map<String, String> headsetCodesToIds;
    private final ConcurrentMap<String, Session> sessionsByHeadsetIds;
    private final ConcurrentMap<String, SyncHandler> syncHandlersByHeadsetIds;
    private final Duration syncInterval;

    private final List<Headset> headsets;

    public OctopuSyncGrpcService(
            Vertx vertx,
            Storage storage,
            Set<EventListener> eventListeners,
            Map<String, String> headsetIdsToCodes,
            Duration syncInterval) {

        this.vertx = Objects.requireNonNull(vertx);
        this.storage = Objects.requireNonNull(storage);
        this.eventListeners = eventListeners;
        this.headsetCodesToIds = invertMap(headsetIdsToCodes);
        this.sessionsByHeadsetIds = new ConcurrentHashMap<>();
        this.syncHandlersByHeadsetIds = new ConcurrentHashMap<>();
        this.headsets = collectHeadsets(headsetIdsToCodes);
        this.syncInterval = syncInterval;
    }

    private static Map<String, String> invertMap(Map<String, String> headsetIdsToCodes) {
        return headsetIdsToCodes.entrySet().stream()
                // let it fail if there are duplicate values
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private List<Headset> collectHeadsets(Map<String, String> headsetIdsToCodes) {
        return headsetIdsToCodes.entrySet().stream()
                .map(e -> Headset.newBuilder()
                        .setId(e.getKey())
                        .setCode(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void getHeadsets(GetHeadsetsRequest request, Future<GetHeadsetsResponse> response) {
        response.complete(GetHeadsetsResponse.newBuilder()
                .addAllHeadsets(headsets)
                .build());
    }

    @Override
    public void createSession(CreateSessionRequest request, Future<CreateSessionResponse> response) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Received create session request: {}", request);
        }

        String headsetCode = request.getHeadsetCode();
        String headsetId = headsetCodesToIds.get(headsetCode);
        if (headsetId == null) {
            response.fail(Status.INVALID_ARGUMENT
                    .withDescription("Headset ID not found for code: " + headsetCode)
                    .asRuntimeException());
            return;
        }
        String deviceId = request.getDeviceId();
        if (deviceId == null) {
            response.fail(Status.INVALID_ARGUMENT
                    .withDescription("Device ID not present in request")
                    .asRuntimeException());
            return;
        }
        Session session = createSession(headsetCode, deviceId);
        Session existingSession = sessionsByHeadsetIds.putIfAbsent(headsetId, session);
        if (existingSession == null) {
            LOGGER.info("Successfully created new session for headset {} ({}): {}", headsetId, headsetCode, session);
            eventListeners.forEach(l -> l.onClientSessionCreated(headsetId));
        } else if (!existingSession.equals(session)) {
            response.fail(Status.FAILED_PRECONDITION
                    .withDescription("Headset code already claimed by someone else: " + headsetCode)
                    .asRuntimeException());
            return;
        }

        response.complete(CreateSessionResponse.newBuilder()
                .setSession(session)
                .build());
    }

    private static Session createSession(String headsetCode, String deviceId) {
        Objects.requireNonNull(headsetCode);
        Objects.requireNonNull(deviceId);

        return Session.newBuilder()
                // deterministic unique ID is based on both headset and user device
                .setId(UUID.nameUUIDFromBytes((headsetCode + deviceId).getBytes(StandardCharsets.UTF_8)).toString())
                .build();
    }

    @Override
    public void sync(GrpcBidiExchange<ClientSyncMessage, ServerSyncMessage> exchange) {
        exchange.exceptionHandler(e -> LOGGER.error("Client bidi exchange failed", e));
        exchange.handler(request -> {
            ClientSyncMessage.MessageCase messageCase = request.getMessageCase();
            switch (messageCase) {
                case SESSION: {
                    Session session = request.getSession();
                    if (session == null) {
                        exchange.fail(Status.INVALID_ARGUMENT
                                .withDescription("`session` request parameter is not set")
                                .asRuntimeException());
                        break;
                    }

                    String headsetId = getHeadsetIdForSession(session);
                    if (headsetId == null) {
                        exchange.fail(Status.UNAUTHENTICATED
                                .withDescription("Unknown session: " + session)
                                .asRuntimeException());
                        break;
                    }

                    SyncHandler handler = new SyncHandler(headsetId, exchange);
                    if (syncHandlersByHeadsetIds.putIfAbsent(headsetId, handler) != null) {
                        exchange.fail(Status.FAILED_PRECONDITION
                                .withDescription("Sync exchange is already extablished for session: " + session)
                                .asRuntimeException());
                    } else {
                        exchange.exceptionHandler(e -> {
                            LOGGER.error("Client bidi exchange failed", e);
                            syncHandlersByHeadsetIds.remove(headsetId);
                            handler.onExchangeError(e);
                            handler.stop();
                        });
                        exchange.endHandler(it -> {
                            syncHandlersByHeadsetIds.remove(headsetId);
                            handler.stop();
                        });
                        handler.start();
                    }
                    break;
                }
                case SYNC_TIME_RESPONSE: {
                    exchange.fail(Status.FAILED_PRECONDITION
                            .withDescription("Unexpected sync time response")
                            .asRuntimeException());
                    break;
                }
                case MESSAGE_NOT_SET: {
                    exchange.fail(Status.INVALID_ARGUMENT
                            .withDescription("Empty message")
                            .asRuntimeException());
                    break;
                }
                default: {
                    exchange.fail(Status.UNKNOWN
                            .withDescription("Unexpected server error: unknown message case " + messageCase)
                            .asRuntimeException());
                    break;
                }
            }
        });
    }

    private class SyncHandler {
        private final GrpcBidiExchange<ClientSyncMessage, ServerSyncMessage> exchange;
        private final ClientTimeSynchronizer timeSynchronizer;

        private SyncHandler(String headsetId, GrpcBidiExchange<ClientSyncMessage, ServerSyncMessage> exchange) {
            this.exchange = exchange;
            this.timeSynchronizer = new ClientTimeSynchronizer(headsetId, exchange, syncInterval);
        }

        public void start() {
            timeSynchronizer.startSync()
                    .flatMapCompletable(r ->
                            Completable.fromAction(() -> {
                                eventListeners.forEach(l -> l.onClientTimeSyncResult(r));
                            }).andThen(storage.save(r))
                    ).subscribe();
        }

        public void stop() {
            timeSynchronizer.stopSync();
        }

        public void onExchangeError(Throwable e) {
            timeSynchronizer.onExchangeError(e);
        }

        public void onExperienceStarted() {
            exchange.write(ServerSyncMessage.newBuilder()
                    .setNotification(Notification.newBuilder()
                            .setExperienceStartedEvent(ExperienceStartedEvent.getDefaultInstance()))
                    .build());
        }

        public void onExperienceStopped() {
            exchange.write(ServerSyncMessage.newBuilder()
                    .setNotification(Notification.newBuilder()
                            .setExperienceStoppedEvent(ExperienceStoppedEvent.getDefaultInstance()))
                    .build());
        }

        // TODO: sending of notifications on signal quality
    }

    @Override
    public void updateState(UpdateStateRequest request, Future<UpdateStateResponse> response) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Received update state request: {}", request);
        }

        long sinceTimeUtc = request.getSinceTimeUtc();
        if (sinceTimeUtc <= 0) {
            response.fail(Status.INVALID_ARGUMENT
                    .withDescription("`since_time_utc` request parameter is invalid or not set: " + sinceTimeUtc)
                    .asRuntimeException());
            return;
        }
        State state = request.getState();
        if (state == State.UNRECOGNIZED) {
            response.fail(Status.INVALID_ARGUMENT
                    .withDescription("`state` request parameter is not set")
                    .asRuntimeException());
            return;
        }
        if (!request.hasSession()) {
            response.fail(Status.INVALID_ARGUMENT
                    .withDescription("`session` request parameter is not set")
                    .asRuntimeException());
            return;
        }

        Session session = request.getSession();
        String headsetId = getHeadsetIdForSession(session);
        if (headsetId == null) {
            response.fail(Status.UNAUTHENTICATED
                    .withDescription("Unknown session: " + session)
                    .asRuntimeException());
            return;
        }

        MoodState moodState = new MoodState(headsetId, state.name(), sinceTimeUtc);
        eventListeners.forEach(l -> l.onClientStateUpdate(moodState));
        storage.save(moodState)
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe(() -> response.complete(UpdateStateResponse.getDefaultInstance()),
                        e -> response.fail(Status.fromThrowable(e)
                                .augmentDescription("Failed to persist mood state: " + state)
                                .asRuntimeException()));
    }

    private @Nullable String getHeadsetIdForSession(Session session) {
        return sessionsByHeadsetIds.entrySet().stream()
                .filter(e -> e.getValue().equals(session))
                .map(Map.Entry::getKey)
                .findAny().orElse(null);
    }

    public synchronized void onExperienceStarted() {
        syncHandlersByHeadsetIds.values()
                .forEach(h -> {
                    try {
                        h.onExperienceStarted();
                    } catch (Exception e) {
                        LOGGER.error("Failed to notify sync handler that experience has started", e);
                    }
                });
    }

    public synchronized void onExperienceStopped() {
        syncHandlersByHeadsetIds.values()
                .forEach(h -> {
                    try {
                        h.onExperienceStopped();
                    } catch (Exception e) {
                        LOGGER.error("Failed to notify sync handler that experience has stopped", e);
                    }
                });
    }
}
