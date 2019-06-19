package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.vertx.core.http.RequestOptions;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.core.http.WebSocket;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEventDecoder;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.json.MessageCoder;
import net.manaty.octopusync.service.emotiv.message.*;
import net.manaty.octopusync.service.emotiv.message.SubscribeResponse.StreamInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CortexClientImpl implements CortexClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexClientImpl.class);

    private static final Pattern SESSION_STOPPED_PATTERN;

    static {
        SESSION_STOPPED_PATTERN = Pattern.compile("All subscriptions of session ([\\w\\d-]+) was stopped by Cortex");
    }

    private final Vertx vertx;
    private final HttpClient httpClient;
    private final RequestOptions websocketOptions;
    private final MessageCoder messageCoder;
    private final AtomicLong idseq;
    private final ConcurrentMap<Long, ResponseObserver<?>> responseObservers;
    private final ConcurrentMap<String, EventObserver> eventObservers;

    private volatile Future<WebSocket> websocketPromise;
    private final Object websocketLock;

    public CortexClientImpl(Vertx vertx, HttpClient httpClient, InetSocketAddress cortexServerAddress, boolean useSsl) {
        this.vertx = vertx;
        this.httpClient = httpClient;
        this.websocketOptions = buildWebsocketOptions(cortexServerAddress, useSsl);
        this.messageCoder = new MessageCoder();
        this.idseq = new AtomicLong(1);
        this.responseObservers = new ConcurrentHashMap<>();
        this.eventObservers = new ConcurrentHashMap<>();
        this.websocketPromise = Future.succeededFuture();
        this.websocketLock = new Object();
    }

    private static RequestOptions buildWebsocketOptions(InetSocketAddress cortexServerAddress, boolean useSsl) {
        return new RequestOptions()
                .setHost(cortexServerAddress.getHostString())
                .setPort(cortexServerAddress.getPort())
                .setSsl(useSsl)
                .setURI("/");
    }

    @Override
    public Completable connect() {
        return getWebsocket().ignoreElement();
    }

    @Override
    public Single<GetUserLoginResponse> getUserLogin() {
        return Single.fromCallable(() -> {
            return new GetUserLoginRequest(idseq.getAndIncrement());
        }).flatMap(request -> executeRequest(request, GetUserLoginResponse.class));
    }

    @Override
    public Single<LoginResponse> login(String username, String password, String clientId, String clientSecret) {
        return Single.fromCallable(() -> {
            return new LoginRequest(idseq.getAndIncrement(), username, password, clientId, clientSecret);
        }).flatMap(request -> executeRequest(request, LoginResponse.class));

    }

    @Override
    public Single<LogoutResponse> logout(String username) {
        return Single.fromCallable(() -> {
            return new LogoutRequest(idseq.getAndIncrement(), username);
        }).flatMap(request -> executeRequest(request, LogoutResponse.class));
    }

    @Override
    public Single<AuthorizeResponse> authorize(String clientId, String clientSecret, String license, int debit) {
        return Single.fromCallable(() -> {
            return new AuthorizeRequest(idseq.getAndIncrement(), clientId, clientSecret, license, debit);
        }).flatMap(request -> executeRequest(request, AuthorizeResponse.class));
    }

    @Override
    public Single<QueryHeadsetsResponse> queryHeadsets() {
        return Single.fromCallable(() -> {
            return new QueryHeadsetsRequest(idseq.getAndIncrement());
        }).flatMap(request -> executeRequest(request, QueryHeadsetsResponse.class));
    }

    @Override
    public Single<QuerySessionsResponse> querySessions(String authzToken, String appId) {
        return Single.fromCallable(() -> {
            return new QuerySessionsRequest(idseq.getAndIncrement(), authzToken, appId);
        }).flatMap(request -> executeRequest(request, QuerySessionsResponse.class));
    }

    @Override
    public Single<CreateSessionResponse> createSession(String authzToken, String headset, Session.Status status) {
        return Single.fromCallable(() -> {
            return new CreateSessionRequest(idseq.getAndIncrement(), authzToken, headset, status.protocolValue());
        }).flatMap(request -> executeRequest(request, CreateSessionResponse.class));
    }

    @Override
    public Single<UpdateSessionResponse> updateSession(String authzToken, String session, Session.Status status) {
        return Single.fromCallable(() -> {
            return new UpdateSessionRequest(idseq.getAndIncrement(), authzToken, session, status.protocolValue());
        }).flatMap(request -> executeRequest(request, UpdateSessionResponse.class));
    }

    @Override
    public Single<SubscribeResponse> subscribe(String authzToken, Set<CortexEventKind> streams, String sessionId, CortexEventListener eventListener) {
        return Single.fromCallable(() -> {
            Set<String> streamNames = streams.stream()
                    .map(s -> s.name().toLowerCase())
                    .collect(Collectors.toSet());
            return new SubscribeRequest(idseq.getAndIncrement(), authzToken, streamNames, sessionId);
        }).flatMap(request -> {
            EventObserver eventObserver = new EventObserver(sessionId, eventListener);
            if (eventObservers.putIfAbsent(sessionId, eventObserver) != null) {
                throw new IllegalStateException("Subscription already exists for session: " + sessionId);
            }
            return executeRequest(request, SubscribeResponse.class)
                    .flatMap(response -> {
                        if (response.error() != null) {
                            eventObservers.values().remove(eventObserver);
                        } else {
                            eventObserver.setStreamInfo(response.result());
                        }
                        return Single.just(response);
                    }).doOnError(e -> eventObservers.values().remove(eventObserver));
        });
    }

    private <R extends Response<?>> Single<R> executeRequest(Request request, Class<R> responseType) {
        return getWebsocket().flatMap(websocket ->
                Single.create(emitter -> {
                    responseObservers.put(request.id(), new ResponseObserver<>(responseType, emitter));
                    // writeTextMessage is thread-safe
                    websocket.writeTextMessage(messageCoder.encodeRequest(request));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Sent {}", request);
                    }
                }));
    }

    private Single<WebSocket> getWebsocket() {
        Future<WebSocket> promise = this.websocketPromise;
        return promise.rxSetHandler()
                .flatMap(websocket -> {
                    if (websocket == null) {
                        synchronized (websocketLock) {
                            // check if someone already initialized reconnect
                            if (promise != this.websocketPromise) {
                                return getWebsocket();
                            } else {
                                this.websocketPromise = connectWebsocket();
                                return this.websocketPromise.rxSetHandler();
                            }
                        }
                    } else {
                        return Single.just(websocket);
                    }
                });
    }

    private Future<WebSocket> connectWebsocket() {
        Future<WebSocket> promise = Future.future();

        vertx.rxExecuteBlocking((Future<WebSocket> future) -> {
            LOGGER.info("Connecting to websocket {}:{}", websocketOptions.getHost(), websocketOptions.getPort());
            httpClient.websocket(websocketOptions, future::complete, future::fail);
        }).doOnSuccess(websocket -> {
            LOGGER.info("Successfully connected to websocket");
            websocket.textMessageHandler(this::processMessage);
            websocket.closeHandler(it -> {
                LOGGER.info("Websocket has been closed");
                resetWebsocket(promise);
            });
            websocket.exceptionHandler(e -> {
                LOGGER.error("Error in websocket connection", e);
                resetWebsocket(promise);
            });
            promise.complete(websocket);
        }).doOnError(e -> {
            LOGGER.error("Failed to connect websocket", e);
            resetWebsocket(promise);
        }).subscribe(it -> {}, e -> {});

        return promise;
    }

    private void resetWebsocket(Future<WebSocket> expected) {
        synchronized (websocketLock) {
            // check if the websocket has already
            // been re-initialized by someone else
            if (expected == this.websocketPromise) {
                this.websocketPromise = Future.future();
            }
        }
    }

    private void processMessage(String message) {
        try {
            OptionalLong id = messageCoder.lookupMessageId(message);
            if (id.isPresent()) {
                ResponseObserver<?> observer = responseObservers.remove(id.getAsLong());
                if (observer == null) {
                    LOGGER.warn("Discarding unexpected response with id {}: {}", id.getAsLong(), message);
                } else {
                    observer.onSuccess(message);
                }
            } else {
                String warning = messageCoder.lookupWarning(message);
                Matcher m;
                if (warning != null && (m = SESSION_STOPPED_PATTERN.matcher(warning)).matches()) {
                    String subscriptionId = Objects.requireNonNull(m.group(1));
                    LOGGER.info("Received termination event for subscription ID {}; message: {}", subscriptionId, message);
                    EventObserver observer = eventObservers.get(subscriptionId);
                    if (observer != null) {
                        observer.onSessionStopped();
                    } else {
                        LOGGER.error("Missing event observer for subscription ID {}, discarding: {}", subscriptionId, message);
                    }
                } else {
                    // must be an event
                    String subscriptionId = messageCoder.lookupSubscriptionId(message);
                    if (subscriptionId == null) {
                        LOGGER.error("Unknown message type (no message ID, no subscription ID), discarding: {}", message);
                    } else {
                        EventObserver observer = eventObservers.get(subscriptionId);
                        if (observer != null) {
                            observer.onEvent(message);
                        } else {
                            LOGGER.error("Missing event observer for subscription ID {}, discarding: {}", subscriptionId, message);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process message: " + message, e);
        }
    }

    private class ResponseObserver<R extends Response<?>> {

        private final Class<R> responseType;
        private final SingleEmitter<R> emitter;

        ResponseObserver(Class<R> responseType, SingleEmitter<R> emitter) {
            this.responseType = responseType;
            this.emitter = emitter;
        }

        public void onSuccess(String message) {
            try {
                R response = messageCoder.decodeResponse(responseType, message);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Received {}", response);
                }
                emitter.onSuccess(response);
            } catch (Exception e) {
                emitter.onError(e);
            }
        }
    }

    private class EventObserver {

        private final String sessionId;
        private final CortexEventListener eventListener;
        private final ConcurrentMap<CortexEventKind, CortexEventDecoder> decodersByEventKind;

        public EventObserver(String sessionId, CortexEventListener eventListener) {
            this.sessionId = Objects.requireNonNull(sessionId);
            this.eventListener = Objects.requireNonNull(eventListener);
            this.decodersByEventKind = new ConcurrentHashMap<>();
        }

        public synchronized void setStreamInfo(List<StreamInfo> streamInfos) {
            for (StreamInfo streamInfo : streamInfos) {
                CortexEventKind eventKind = CortexEventKind.forName(streamInfo.getStream());
                switch (eventKind) {
                    case EEG:
                    case DEV:
                    case MOT: {
                        decodersByEventKind.put(eventKind, messageCoder.createEventDecoder(eventKind, streamInfo));
                        break;
                    }
                    default: {
                        LOGGER.warn("Unexpected stream type {}, ignoring...", streamInfo.getStream());
                        break;
                    }
                }
            }
        }

        public synchronized void onEvent(String eventText) {
            decodersByEventKind.forEach((eventKind, decoder) -> {
                CortexEvent event = decoder.decode(eventText);
                if (event != null) {
                    eventListener.onEvent(event);
                }
            });
        }

        public synchronized void onSessionStopped() {
            eventListener.onSessionStopped(sessionId);
        }
    }
}