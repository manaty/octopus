package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.service.emotiv.message.AuthorizeResponse;
import net.manaty.octopusync.service.emotiv.message.GetUserLoginResponse;
import net.manaty.octopusync.service.emotiv.message.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class CortexAuthenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexAuthenticator.class);

    // TODO: configurable?
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(10);

    private enum State {
        INITIAL, UNAUTHENTICATED, AUTHENTICATED, AUTHORIZED
    }

    private final Vertx vertx;
    private final CortexClient client;
    private final EmotivCredentials credentials;
    // number of sessions to request from Cortex
    private final int sessionDebit;

    private final AtomicBoolean started;

    private volatile State state;
    private volatile Future<String> authzTokenPromise;

    public CortexAuthenticator(Vertx vertx, CortexClient client, EmotivCredentials credentials, int sessionDebit) {
        this.vertx = vertx;
        this.client = client;
        this.credentials = credentials;
        this.sessionDebit = sessionDebit;

        this.started = new AtomicBoolean(false);
        this.state = State.INITIAL;
        this.authzTokenPromise = Future.future();
    }

    public Single<String> getAuthzToken() {
        return authzTokenPromise.rxSetHandler();
    }

    public Completable start() {
        return Completable.fromAction(() -> {
            if (started.compareAndSet(false, true)) {
                executeNextStep();
            }
        });
    }

    public Completable reset() {
        return Completable.fromAction(() -> {
            // TODO: reset token promise and perform auth/authz from scratch
            throw new UnsupportedOperationException("CortexAuthenticator.reset() not implemented yet");
        });
    }

    public Completable stop() {
        return Completable.fromAction(() -> {
            started.set(false);
        });
    }

    private void executeNextOrRetryCurrentStepWithDelay() {
        vertx.setTimer(RETRY_INTERVAL.toMillis(), it -> executeNextStep());
    }

    private void executeNextStep() {
        if (started.get()) {
            switch (state) {
                case INITIAL: {
                    getLoggedInUsers();
                    break;
                }
                case UNAUTHENTICATED: {
                    login();
                    break;
                }
                case AUTHENTICATED: {
                    authorize();
                    break;
                }
                case AUTHORIZED: {
                    // TODO: monitor state changes periodically (in case auth expires or something)
                    executeNextOrRetryCurrentStepWithDelay();
                    break;
                }
            }
        }
    }

    // ---- Get list of currently logged in users ---- //

    private void getLoggedInUsers() {
        Single<GetUserLoginResponse> promise = client.getUserLogin();

        promise.doOnSuccess(this::onGetLoggedInUsersResponse)
                .doOnError(this::onGetLoggedInUsersError)
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe();
    }

    private void onGetLoggedInUsersResponse(GetUserLoginResponse response) {
        if (response.error() != null) {
            ResponseErrors error = ResponseErrors.byCode(response.error().getCode());
            LOGGER.error("Request for logged in users failed: {}", error);
            executeNextOrRetryCurrentStepWithDelay();
        } else {
            if (response.result().isEmpty()) {
                state = State.UNAUTHENTICATED;
                executeNextStep();
            } else if (response.result().contains(credentials.getUsername())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("User {} with client ID {} is already logged in",
                            credentials.getUsername(), credentials.getClientId());
                }
                state = State.AUTHENTICATED;
                executeNextStep();
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Curent user {} is not logged in; users that are logged in: {}",
                            credentials.getUsername(), response.result());
                }
                Observable.fromIterable(response.result())
                        .flatMapCompletable(username -> {
                            return client.logout(username)
                                    .doOnError(e -> {
                                        LOGGER.error("Failed to logout username: " + username, e);
                                    }).ignoreElement();
                        })
                        .doOnComplete(() -> {
                            state = State.UNAUTHENTICATED;
                            executeNextStep();
                        })
                        .doOnError(e -> executeNextOrRetryCurrentStepWithDelay())
                        .subscribeOn(RxHelper.blockingScheduler(vertx))
                        .subscribe();
            }
        }
    }

    private void onGetLoggedInUsersError(Throwable e) {
        LOGGER.error("Request for logged in users failed", e);
        executeNextOrRetryCurrentStepWithDelay();
    }

    // ---- Login ---- //

    private void login() {
        Single<LoginResponse> promise = client.login(
                credentials.getUsername(),
                credentials.getPassword(),
                credentials.getClientId(),
                credentials.getClientSecret());

        promise.doOnSuccess(this::onLoginResponse)
                .doOnError(this::onLoginError)
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe();
    }

    private void onLoginResponse(LoginResponse response) {
        if (response.error() != null) {
            ResponseErrors error = ResponseErrors.byCode(response.error().getCode());
            LOGGER.error("Login with username {} and client ID {} failed: {}",
                    credentials.getUsername(), credentials.getClientId(), error);
            // need to distinguish temporary issues (like absence of Internet connection)
            // from unsolvable programming/configuration errors (like invalid auth configuration)
            switch (error) {
                case MISSING_CLIENT_ID:
                case INVALID_CREDENTIALS:
                case INVALID_CLIENT_ID_OR_SECRET: {
                    LOGGER.error("Critical error, terminating work...");
                    break;
                }
                default: {
                    executeNextOrRetryCurrentStepWithDelay();
                    break;
                }
            }
        } else {
            state = State.AUTHENTICATED;
            executeNextStep();
        }
    }

    private void onLoginError(Throwable e) {
        LOGGER.error("Login with username " + credentials.getUsername() + "," +
                " client ID " + credentials.getClientId() + " failed", e);
        executeNextOrRetryCurrentStepWithDelay();
    }

    // ---- Authorize and receive a token for premium API ---- //

    private void authorize() {
        Single<AuthorizeResponse> promise = client.authorize(
                credentials.getClientId(),
                credentials.getClientSecret(),
                credentials.getLicense(),
                sessionDebit);

        promise.doOnSuccess(this::onAuthorizeResponse)
                .doOnError(this::onAuthorizeError)
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe();
    }

    private void onAuthorizeResponse(AuthorizeResponse response) {
        if (response.error() != null) {
            ResponseErrors error = ResponseErrors.byCode(response.error().getCode());
            LOGGER.error("Authorize with client ID {}, license key {} and debit {} failed: {}",
                    credentials.getClientId(), credentials.getLicense(), sessionDebit, error);
            switch (error) {
                case MISSING_CLIENT_ID:
                case INVALID_CLIENT_ID_OR_SECRET:
                case INVALID_LICENSE:
                case LICENSE_EXPIRED:
                case LICENSE_USAGE_FORBIDDEN:
                case LICENSE_DEBIT_INSUFFICIENT:
                case DAILY_DEBIT_INSUFFICIENT: {
                    break;
                }
                default: {
                    executeNextOrRetryCurrentStepWithDelay();
                    break;
                }
            }
        } else {
            String authzToken = response.result().getToken();
            authzTokenPromise.complete(authzToken);
            state = State.AUTHORIZED;

            executeNextStep();
        }
    }

    private void onAuthorizeError(Throwable e) {
        LOGGER.error("Authorize with client ID " + credentials.getClientId() + "," +
                " license key " + credentials.getLicense() + "," +
                " debit " + sessionDebit + " failed", e);
        executeNextOrRetryCurrentStepWithDelay();
    }
}
