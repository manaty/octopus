package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.message.QuerySessionsResponse;
import net.manaty.octopusync.service.emotiv.message.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CortexServiceImpl implements CortexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexServiceImpl.class);

    private final Vertx vertx;
    private final CortexClient client;
    private final Map<String, String> headsetIdsToCodes;
    private final CortexAuthenticator authenticator;

    public CortexServiceImpl(
            Vertx vertx,
            CortexClient client,
            EmotivCredentials credentials,
            Map<String, String> headsetIdsToCodes) {

        this.vertx = vertx;
        this.client = client;
        this.headsetIdsToCodes = headsetIdsToCodes;
        this.authenticator = new CortexAuthenticator(vertx, client, credentials, headsetIdsToCodes.size());
    }

    @Override
    public Completable startCapture() {
        return Completable.concatArray(
                authenticator
                        .onNewAuthzTokenIssued(this::onNewAuthzTokenIssued)
                        .start());
    }

    private void onNewAuthzTokenIssued(String authzToken) {
        client.querySessions(authzToken)
                .doOnSuccess(response -> onQuerySessionsResponse(authzToken, response))
                .doOnError(this::onQuerySessionsError)
                .subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe();
    }

    private void onQuerySessionsResponse(String authzToken, QuerySessionsResponse response) {
        if (response.error() != null) {
            ResponseErrors error = ResponseErrors.byCode(response.error().getCode());
            LOGGER.error("Query for sessions failed: {}", error);
            // TODO
//            executeNextOrRetryCurrentStepWithDelay();
        } else {
            Set<String> headsetIds = new HashSet<>(headsetIdsToCodes.keySet());

            List<Single<String>> updatesForExistingSessions = response.result().stream()
                    .filter(session -> {
                        String headsetId = session.getHeadset().getId();
                        boolean known = headsetIds.remove(headsetId);
                        if (!known) {
                            LOGGER.info("Session {} for unknown headset {}, skipping...", session.getId(), headsetId);
                        }
                        return known;
                    })
                    .map(session -> {
                        String sessionId = session.getId();
                        String headsetId = session.getHeadset().getId();
                        Session.Status status = Session.getStatus(session);
                        switch (status) {
                            case CLOSED:
                            case OPENED: {
                                LOGGER.info("Session {} for headset {} has '{}' status, will activate...",
                                        sessionId, headsetId, status);
                                return updateSession(authzToken, sessionId, headsetId);
                            }
                            case ACTIVATED: {
                                LOGGER.info("Session {} for headset {} is already active, skipping...",
                                        session.getId(), session.getHeadset().getId());
                                return Single.just(session.getId());
                            }
                            default: {
                                throw new IllegalStateException("Unknown session status: " + status);
                            }
                        }
                    })
                    .collect(Collectors.toList());

            List<Single<String>> promisesForNewSessions = headsetIds.stream()
                    .map(headsetId -> {
                        LOGGER.info("No session exists for headset {}, will create and activate...", headsetId);
                        return createSession(authzToken, headsetId);
                    })
                    .collect(Collectors.toList());

            Single.concat(promisesForNewSessions)
                    .concatWith(Single.concat(updatesForExistingSessions))
                    .window(1)
                    .flatMapCompletable(f -> f
                            .flatMapCompletable(sessionId -> subscribe(authzToken, sessionId))
                    )
                    .doOnError(e -> {
                        // TODO
                    })
                    .subscribe();

        }
    }

    private void onQuerySessionsError(Throwable e) {
        // TODO
    }

    private Single<String> updateSession(String authzToken, String sessionId, String headsetId) {
        return client.updateSession(authzToken, sessionId, Session.Status.ACTIVATED)
                .flatMap(updateResponse -> {
                    if (updateResponse.error() != null) {
                        return Single.error(new IllegalStateException(
                                "Failed to activate session "+sessionId+
                                        " for headset "+headsetId+": " + updateResponse.error()));
                    } else {
                        return Single.just(updateResponse.result().getId());
                    }
                });
    }

    private Single<String> createSession(String authzToken, String headsetId) {
        return client.createSession(authzToken, headsetId, Session.Status.ACTIVATED)
                .flatMap(createResponse -> {
                    if (createResponse.error() != null) {
                        return Single.error(new IllegalStateException(
                                "Failed to create and activate session for headset "+headsetId+": " +
                                        createResponse.error()));
                    } else {
                        return Single.just(createResponse.result().getId());
                    }
                });
    }

    private Completable subscribe(String authzToken, String sessionId) {
        return client.subscribe(authzToken, Collections.singleton("eeg"), sessionId, event -> onEvent(sessionId, event))
                .flatMapCompletable(subscribeResponse -> {
                    if (subscribeResponse.error() != null) {
                        return Completable.error(new IllegalStateException(
                                "Failed to subscribe to events for session "+sessionId+": " + subscribeResponse.error()));
                    } else {
                        return Completable.complete();
                    }
                });
    }

    private void onEvent(String sessionId, CortexEvent event) {
        // TODO: implement
    }

    @Override
    public Completable stopCapture() {
        return Completable.concatArray(authenticator.stop());
    }
}
