package net.manaty.octopusync.service.grpc;

import io.grpc.Status;
import io.vertx.core.Future;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.api.*;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.service.db.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OctopuSyncGrpcService extends OctopuSyncGrpc.OctopuSyncVertxImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OctopuSyncGrpcService.class);

    private final Vertx vertx;
    private final Storage storage;
    private final Map<String, String> headsetCodesToIds;
    private final Map<String, Session> sessionsByHeadsetCodes;

    public OctopuSyncGrpcService(Vertx vertx, Storage storage, Map<String, String> headsetIdsToCodes) {
        this.vertx = Objects.requireNonNull(vertx);
        this.storage = Objects.requireNonNull(storage);
        this.headsetCodesToIds = invertMap(headsetIdsToCodes);
        this.sessionsByHeadsetCodes = new ConcurrentHashMap<>();
    }

    private static Map<String, String> invertMap(Map<String, String> headsetIdsToCodes) {
        return headsetIdsToCodes.entrySet().stream()
                // let it fail if there are duplicate values
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
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
        Session existingSession = sessionsByHeadsetCodes.putIfAbsent(headsetId, session);
        if (existingSession == null) {
            LOGGER.info("Successfully created new session for headset {} ({}): {}", headsetId, headsetCode, session);
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
        State moodState = request.getState();
        if (moodState == State.UNRECOGNIZED) {
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
        String headsetCode = sessionsByHeadsetCodes.entrySet().stream()
                .filter(e -> e.getValue().equals(session))
                .map(Map.Entry::getKey)
                .findAny().orElse(null);

        if (headsetCode == null) {
            response.fail(Status.UNAUTHENTICATED
                    .withDescription("Unknown session: " + session)
                    .asRuntimeException());
            return;
        }

        String headsetId = headsetCodesToIds.get(headsetCode);
        storage.save(new MoodState(headsetId, moodState.name(), sinceTimeUtc))
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe(() -> response.complete(UpdateStateResponse.getDefaultInstance()),
                        e -> response.fail(Status.fromThrowable(e)
                                .augmentDescription("Failed to persist mood state: " + moodState)
                                .asRuntimeException()));
    }
}
