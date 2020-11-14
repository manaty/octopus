package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.service.emotiv.message.AuthorizeResponse;
import net.manaty.octopusync.service.emotiv.message.GetUserLoginResponse;
import net.manaty.octopusync.service.emotiv.message.RequestAccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class CortexAuthenticator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexAuthenticator.class);

    // TODO: configurable?
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(10);

    private enum State {
        CHECKING_ACCESS,
        CHECKING_AUTHENTICATION,
        AUTHENTICATED,
        AUTHORIZED
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
        this.state = State.CHECKING_ACCESS;
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
                case CHECKING_ACCESS: {
                    requestAccess();
                    break;
                }
                case CHECKING_AUTHENTICATION: {
                    getLoggedInUsers();
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

    // ---- Request access to Emotiv application ---- //

    private void requestAccess() {
        Single<RequestAccessResponse> promise = client.requestAccess(
                credentials.getClientId(), credentials.getClientSecret());

        promise.doOnSuccess(this::onRequestAccessResponse)
                .doOnError(this::onRequestAccessError)
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe();
    }

    private void onRequestAccessResponse(RequestAccessResponse response) {
        if (response.error() != null) {
            ResponseErrors error = ResponseErrors.byCode(response.error().getCode());
            LOGGER.error("Request for access failed: {}", error);
            executeNextOrRetryCurrentStepWithDelay();
        } else if (response.result().isAccessGranted()) {
            state = State.CHECKING_AUTHENTICATION;
            executeNextStep();
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Access is not granted for client ID {}",
                        credentials.getClientId());
            }
            executeNextOrRetryCurrentStepWithDelay();
        }
    }

    private void onRequestAccessError(Throwable e) {
        LOGGER.error("Request for access failed", e);
        executeNextOrRetryCurrentStepWithDelay();
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
                LOGGER.warn("Curent user {} is not logged in", credentials.getUsername());
                executeNextOrRetryCurrentStepWithDelay();
            } else if (response.result().stream()
                    .map(GetUserLoginResponse.User::getUsername)
                    .anyMatch(username -> credentials.getUsername().equals(username))) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("User {} is already logged in", credentials.getUsername());
                }
                state = State.AUTHENTICATED;
                executeNextStep();
            } else {
                LOGGER.warn("Curent user {} is not logged in; users that are logged in: {}",
                        credentials.getUsername(), response.result());
                executeNextOrRetryCurrentStepWithDelay();
            }
        }
    }

    private void onGetLoggedInUsersError(Throwable e) {
        LOGGER.error("Request for logged in users failed", e);
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
            AuthorizeResponse.AuthTokenHolder.Warning warning;
            if ((warning = response.result().getWarning()) != null) {
                LOGGER.warn("Authorize request returned: {}", warning);
            }

            String authzToken;
            if ((authzToken = response.result().getToken()) != null) {
                authzTokenPromise.complete(authzToken);
                state = State.AUTHORIZED;
                executeNextStep();
            } else {
                executeNextOrRetryCurrentStepWithDelay();
            }
        }
    }

    private void onAuthorizeError(Throwable e) {
        LOGGER.error("Authorize with client ID " + credentials.getClientId() + "," +
                " license key " + credentials.getLicense() + "," +
                " debit " + sessionDebit + " failed", e);
        executeNextOrRetryCurrentStepWithDelay();
    }
}
