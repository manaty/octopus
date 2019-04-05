package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
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

public class CortexServiceImpl implements CortexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexServiceImpl.class);

    // TODO: configurable?
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(10);

    private final Vertx vertx;
    private final CortexClient client;
    private final Set<String> headsetIds;
    private final CortexAuthenticator authenticator;

    private volatile @Nullable CortexSubscriptionManager subscriptionManager;
    private volatile @Nullable CortexEventListener eventListener;

    public CortexServiceImpl(
            Vertx vertx,
            CortexClient client,
            EmotivCredentials credentials,
            Set<String> headsetIds) {

        this.vertx = vertx;
        this.client = client;
        this.headsetIds = headsetIds;
        this.authenticator = new CortexAuthenticator(vertx, client, credentials, headsetIds.size());
    }

    @Override
    public Completable startCapture() {
        return authenticator.start()
                .doOnComplete(this::retrieveAuthzToken);
    }

    // ---- Retrieve authorization token ---- //

    private void retrieveAuthzToken() {
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

    // ---- Query existing sessions for the authorization token ---- //

    private void querySessions(String authzToken) {
        client.querySessions(authzToken)
                .doOnSuccess(response -> onQuerySessionsResponse(authzToken, response))
                .doOnError(this::onQuerySessionsError)
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe();
    }

    private void onQuerySessionsResponse(String authzToken, QuerySessionsResponse response) {
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
                    onQuerySessionsError(new IllegalStateException(error.toString()));
                    break;
                }
            }
        } else {
            eventListener = new CortexEventListenerImpl();
            subscriptionManager = new CortexSubscriptionManager(
                    client, authzToken, response.result(), headsetIds, eventListener);
            subscriptionManager.start()
                    .subscribe();
            // reactive chain completes here,
            // other actions will be invoked only by the event listener
        }
    }

    private void onQuerySessionsError(Throwable e) {
        LOGGER.error("Query for sessions produced a critical error, terminating work...", e);
    }

    @Override
    public Completable stopCapture() {
        CortexSubscriptionManager subscriptionManager = this.subscriptionManager;
        return Completable.concatArray(
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
    }

    private class CortexEventListenerImpl implements CortexEventListener {
        @Override
        public void onEvent(CortexEvent event) {

        }

        @Override
        public void onError(Response.ResponseError error) {

        }

        @Override
        public void onError(Throwable e) {

        }
    }
}
