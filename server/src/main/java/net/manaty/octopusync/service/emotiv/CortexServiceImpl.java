package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.processors.PublishProcessor;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.message.QuerySessionsResponse;
import net.manaty.octopusync.service.emotiv.message.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class CortexServiceImpl implements CortexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexServiceImpl.class);

    // TODO: configurable?
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(10);

    private final Vertx vertx;
    private final CortexClient client;
    private final Set<String> headsetIds;
    private final CortexAuthenticator authenticator;

    private final AtomicBoolean started;

    private volatile @Nullable CortexSubscriptionManager subscriptionManager;
    private volatile @Nullable CortexEventListener eventListener;

    private volatile PublishProcessor<CortexEvent> resultProcessor;

    public CortexServiceImpl(
            Vertx vertx,
            CortexClient client,
            EmotivCredentials credentials,
            Set<String> headsetIds) {

        this.vertx = vertx;
        this.client = client;
        this.headsetIds = headsetIds;
        this.authenticator = new CortexAuthenticator(vertx, client, credentials, headsetIds.size());
        this.started = new AtomicBoolean(false);
    }

    @Override
    public Observable<CortexEvent> startCapture() {
        return Observable.defer(() -> {
            if (started.compareAndSet(false, true)) {
                return authenticator.start()
                        .doOnComplete(this::retrieveAuthzToken)
                        .andThen(Observable.defer(() -> {
                            PublishProcessor<CortexEvent> resultProcessor = PublishProcessor.create();
                            this.resultProcessor = resultProcessor;
                            return Observable.fromPublisher(resultProcessor);
                        }));
            } else {
                throw new IllegalStateException("Already started");
            }
        });
    }

    // ---- Retrieve authorization token ---- //

    private void retrieveAuthzToken() {
        if (started.get()) {
            authenticator.getAuthzToken()
                    .doOnSuccess(this::querySessions)
                    .doOnError(e -> {
                        LOGGER.error("Failed to retrieve authz token, will retry shortly...", e);
                        vertx.setTimer(RETRY_INTERVAL.toMillis(), it -> {
                            authenticator.reset()
                                    .doOnComplete(this::retrieveAuthzToken)
                                    .subscribe();
                        });
                    })
                    .subscribeOn(RxHelper.blockingScheduler(vertx))
                    .subscribe();
        }
    }

    // ---- Query existing sessions for the authorization token ---- //

    private void querySessions(String authzToken) {
        if (started.get()) {
            client.querySessions(authzToken)
                    .doOnSuccess(response -> onQuerySessionsResponse(authzToken, response))
                    .doOnError(e -> onQuerySessionsError(authzToken, e))
                    .subscribeOn(RxHelper.blockingScheduler(vertx))
                    .subscribe();
        }
    }

    private void onQuerySessionsResponse(String authzToken, QuerySessionsResponse response) {
        if (started.get()) {
            if (response.error() != null) {
                ResponseErrors error = ResponseErrors.byCode(response.error().getCode());
                switch (error) {
                    case INVALID_AUTH_TOKEN:
                    case AUTH_TOKEN_EXPIRED:
                    case TOKEN_DOES_NOT_MATCH_USER: {
                        LOGGER.error("Failed to query for sessions due to authz issue, will re-authorize..." +
                                " Cortex error was: {}", error);
                        authenticator.reset()
                                .doOnComplete(this::retrieveAuthzToken)
                                .subscribe();
                        break;
                    }
                    case REQUEST_TIMEOUT:
                    case INTERNAL_JSONRPC_ERROR:
                    case UNKNOWN_ERROR: {
                        LOGGER.error("Failed to query for sessions due to potentially recoverable issue, will retry shortly..." +
                                " Cortex error was: {}", error);
                        vertx.setTimer(RETRY_INTERVAL.toMillis(), it -> querySessions(authzToken));
                        break;
                    }
                    default: {
                        onQuerySessionsError(authzToken, new IllegalStateException(error.toString()));
                        break;
                    }
                }
            } else {
                eventListener = new CortexEventListenerImpl(resultProcessor);
                subscriptionManager = new CortexSubscriptionManager(
                        vertx, client, authzToken, response.result(), headsetIds, eventListener);
                subscriptionManager.start()
                        .subscribe();
                // reactive chain completes here,
                // other actions will be invoked only by the event listener
            }
        }
    }

    private void onQuerySessionsError(String authzToken, Throwable e) {
        LOGGER.error("Failed to query for sessions due to potentially recoverable issue, will retry shortly...", e);
        vertx.setTimer(RETRY_INTERVAL.toMillis(), it -> querySessions(authzToken));
    }

    @Override
    public Completable stopCapture() {
        CortexSubscriptionManager subscriptionManager = this.subscriptionManager;
        return Completable.defer(() -> {
            if (started.compareAndSet(true, false)) {
                return Completable.concatArray(
                        Completable.fromAction(() -> {
                            resultProcessor.onComplete();
                            resultProcessor = null;
                        }),
                        Completable.defer(() -> {
                            if (subscriptionManager != null) {
                                return subscriptionManager.stop();
                            } else {
                                return Completable.complete();
                            }
                        }),
                        authenticator.stop()
                ).doOnError(e -> {
                    LOGGER.error("Unexpected error", e);
                }).onErrorComplete();
            } else {
                throw new IllegalStateException("Not started");
            }
        });
    }

    private class CortexEventListenerImpl implements CortexEventListener {

        private final PublishProcessor<CortexEvent> resultProcessor;

        public CortexEventListenerImpl(PublishProcessor<CortexEvent> resultProcessor) {
            this.resultProcessor = resultProcessor;
        }

        @Override
        public void onEvent(CortexEvent event) {
            resultProcessor.onNext(event);
        }

        @Override
        public void onError(Response.ResponseError error) {
            // TODO: retry or restart or terminate based on error code
            LOGGER.error("Unexpected error: {}", error);
        }

        @Override
        public void onError(Throwable e) {
            // TODO: restart
            LOGGER.error("Unexpected error", e);
        }
    }
}
