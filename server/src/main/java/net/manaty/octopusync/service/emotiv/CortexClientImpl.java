package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.vertx.core.http.RequestOptions;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.core.http.WebSocket;
import net.manaty.octopusync.service.emotiv.message.LoginRequest;
import net.manaty.octopusync.service.emotiv.message.LoginResponse;
import net.manaty.octopusync.service.emotiv.message.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CortexClientImpl implements CortexClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexClientImpl.class);

    private final Vertx vertx;
    private final HttpClient httpClient;
    private final RequestOptions websocketOptions;
    private final MessageCoder messageCoder;
    private final AtomicLong idseq;
    private final ConcurrentHashMap<Long, ResponseObserver<?>> responseObservers;

    private volatile Maybe<WebSocket> websocket;
    private final Object websocketLock;

    public CortexClientImpl(Vertx vertx, HttpClient httpClient, InetSocketAddress cortexServerAddress) {
        this.vertx = vertx;
        this.httpClient = httpClient;
        this.websocketOptions = buildWebsocketOptions(cortexServerAddress);
        this.messageCoder = new MessageCoder();
        this.idseq = new AtomicLong(1);
        this.responseObservers = new ConcurrentHashMap<>();
        this.websocket = Maybe.empty();
        this.websocketLock = new Object();
    }

    private static RequestOptions buildWebsocketOptions(InetSocketAddress cortexServerAddress) {
        return new RequestOptions()
                .setHost(cortexServerAddress.getHostString())
                .setPort(cortexServerAddress.getPort())
                .setSsl(true)
                .setURI("/");
    }

    @Override
    public Completable connect() {
        return getWebsocket().ignoreElement();
    }

    @Override
    public Single<LoginResponse> login(String username, String password, String clientId, String clientSecret) {
        return getWebsocket().flatMap(websocket ->
                Single.create(emitter -> {
                    LoginRequest request = new LoginRequest(
                            idseq.getAndIncrement(), username, password, clientId, clientSecret);
                    responseObservers.put(request.id(), new ResponseObserver<>(LoginResponse.class, emitter));
                    websocket.writeTextMessage(messageCoder.encodeRequest(request));
                }));
    }

    private Single<WebSocket> getWebsocket() {
        Maybe<WebSocket> websocket = this.websocket;
        return websocket.isEmpty()
                .flatMap(empty -> {
                    if (empty) {
                        synchronized (websocketLock) {
                            // check if someone already initialized reconnect
                            if (websocket != this.websocket) {
                                return getWebsocket();
                            } else {
                                this.websocket = connectWebsocket();
                                return this.websocket.toSingle();
                            }
                        }
                    }
                    return websocket.toSingle();
                });
    }

    private Maybe<WebSocket> connectWebsocket() {
        return vertx.rxExecuteBlocking((Future<WebSocket> future) -> {
            LOGGER.info("Connecting to websocket {}:{}", websocketOptions.getHost(), websocketOptions.getPort());
            httpClient.websocket(websocketOptions, future::complete, future::fail);
        }).doOnSuccess(websocket -> {
            LOGGER.info("Successfully connected to websocket");
            websocket.textMessageHandler(this::processResponse);
            websocket.closeHandler(it -> {
                LOGGER.info("Websocket has been closed");
                resetWebsocket();
            });
            websocket.exceptionHandler(e -> {
                LOGGER.error("Error in websocket connection", e);
                resetWebsocket();
            });
        }).doOnError(e -> {
            LOGGER.error("Failed to connect websocket", e);
            resetWebsocket();
        });
    }

    private void resetWebsocket() {
        synchronized (websocketLock) {
            this.websocket = Maybe.empty();
        }
    }

    private void processResponse(String responseText) {
        try {
            long id = messageCoder.lookupMessageId(responseText);
            ResponseObserver<?> observer = responseObservers.get(id);
            if (observer == null) {
                LOGGER.warn("Discarding unexpected response: {}", id, responseText);
            } else {
                observer.onSuccess(responseText);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to process response: " + responseText, e);
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
                emitter.onSuccess(response);
            } catch (Exception e) {
                emitter.onError(e);
            }
        }
    }
}