package net.manaty.octopusync.service.emotiv;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.service.emotiv.message.AuthorizeResponse;
import net.manaty.octopusync.service.emotiv.message.GetUserLoginResponse;
import net.manaty.octopusync.service.emotiv.message.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class CortexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexService.class);

    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(10);

    private enum State {
        INITIAL, UNAUTHENTICATED, AUTHENTICATED, AUTHORIZED
    }

    private final Vertx vertx;
    private final CortexClient cortexClient;
    private final EmotivCredentials credentials;
    private final Map<String, String> headsetIdsToCodes;

    // number of sessions to request from Cortex
    private final int debit;

    private volatile State state;
    private volatile Optional<String> authzToken;

    public CortexService(
            Vertx vertx,
            CortexClient cortexClient,
            EmotivCredentials credentials,
            Map<String, String> headsetIdsToCodes) {

        this.vertx = vertx;
        this.cortexClient = cortexClient;
        this.credentials = credentials;
        this.headsetIdsToCodes = headsetIdsToCodes;
        this.debit = headsetIdsToCodes.size();

        this.state = State.INITIAL;
        this.authzToken = Optional.empty();
    }

    // TODO: should return Events (when the model becomes clear)
    public Observable<Void> startCapture() {
        executeNextStep();
        return Observable.empty();
    }

    public void stopCapture() {

    }

    // TODO: event
    private void retryCurrentStepWithDelay() {
        vertx.setTimer(RETRY_INTERVAL.toMillis(), it -> executeNextStep(state, this));
    }

    // TODO: event
    private void executeNextStep() {
        executeNextStep(state, this);
    }

    // TODO: event
    private void executeNextStepWithDelay() {
        vertx.setTimer(RETRY_INTERVAL.toMillis(), it -> executeNextStep(state, this));
    }

    // TODO: extract Auth/Authz procedure into a separate entity
    private static void executeNextStep(State state, CortexService service) {
        switch (state) {
            case INITIAL: {
                service.getLoggedInUsers();
                break;
            }
            case UNAUTHENTICATED: {
                service.login();
                break;
            }
            case AUTHENTICATED: {
                service.authorize();
                break;
            }
            case AUTHORIZED: {
                // TODO: should monitor state changes periodically (in case auth expires or something)
                service.executeNextStepWithDelay();
                break;
            }
        }
    }

    // ---- Get list of currently logged in users ---- //

    private void getLoggedInUsers() {
        Single<GetUserLoginResponse> promise = cortexClient.getUserLogin();

        promise.doOnSuccess(this::onGetLoggedInUsersResponse)
                .doOnError(this::onGetLoggedInUsersError)
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe();
    }

    private void onGetLoggedInUsersResponse(GetUserLoginResponse response) {
        if (response.error() != null) {
            ResponseErrors error = ResponseErrors.byCode(response.error().getCode());
            LOGGER.error("Request for logged in users failed: {}", error);
            retryCurrentStepWithDelay();
        } else {
            if (response.result().contains(credentials.getUsername())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("User {} with client ID {} is already logged in",
                            credentials.getUsername(), credentials.getClientId());
                }
                state = State.AUTHENTICATED;
            } else {
                state = State.UNAUTHENTICATED;
            }
            executeNextStep();
        }
    }

    private void onGetLoggedInUsersError(Throwable e) {
        LOGGER.error("Request for logged in users failed", e);
        retryCurrentStepWithDelay();
    }

    // ---- Login ---- //

    private void login() {
        Single<LoginResponse> promise = cortexClient.login(
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
                    retryCurrentStepWithDelay();
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
        retryCurrentStepWithDelay();
    }

    // ---- Authorize and receive a token for premium API ---- //

    private void authorize() {
        Single<AuthorizeResponse> promise = cortexClient.authorize(
                credentials.getClientId(),
                credentials.getClientSecret(),
                credentials.getLicense(),
                debit);

        promise.doOnSuccess(this::onAuthorizeResponse)
                .doOnError(this::onAuthorizeError)
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe();
    }

    private void onAuthorizeResponse(AuthorizeResponse response) {
        if (response.error() != null) {
            ResponseErrors error = ResponseErrors.byCode(response.error().getCode());
            LOGGER.error("Authorize with client ID {}, license key {} and debit {} failed: {}",
                    credentials.getClientId(), credentials.getLicense(), debit, error);
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
                    retryCurrentStepWithDelay();
                    break;
                }
            }
        } else {
            authzToken = Optional.of(response.result().getToken());
            state = State.AUTHORIZED;
            executeNextStep();
        }
    }

    private void onAuthorizeError(Throwable e) {
        LOGGER.error("Authorize with client ID " + credentials.getClientId() + "," +
                        " license key " + credentials.getLicense() + "," +
                        " debit " + debit + " failed", e);
        retryCurrentStepWithDelay();
    }
}
