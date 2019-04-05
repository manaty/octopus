package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Single;
import net.manaty.octopusync.service.emotiv.message.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CortexSubscriptionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexSubscriptionManager.class);

    private final CortexClient client;
    private final String authzToken;
    private final List<Session> existingSessions;
    private final Set<String> headsetIds;
    private final CortexEventListener eventListener;

    public CortexSubscriptionManager(
            CortexClient client,
            String authzToken,
            List<Session> existingSessions,
            Set<String> headsetIds,
            CortexEventListener eventListener) {

        this.client = client;
        this.authzToken = authzToken;
        this.existingSessions = existingSessions;
        this.headsetIds = headsetIds;
        this.eventListener = eventListener;
    }

    public Completable start() {
        return Completable.fromAction(() -> {
            Set<String> headsetIds = new HashSet<>(this.headsetIds);

            List<Single<Session>> updatesForExistingSessions = existingSessions.stream()
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
                                return updateSession(authzToken, session, Session.Status.ACTIVATED);
                            }
                            case ACTIVATED: {
                                LOGGER.info("Session {} for headset {} is already active, skipping...",
                                        session.getId(), session.getHeadset().getId());
                                return Single.just(session);
                            }
                            default: {
                                throw new IllegalStateException("Unknown session status: " + status);
                            }
                        }
                    })
                    .collect(Collectors.toList());

            List<Single<Session>> promisesForNewSessions = headsetIds.stream()
                    .map(headsetId -> {
                        LOGGER.info("No session exists for headset {}, will create and activate...", headsetId);
                        return createSession(authzToken, headsetId, Session.Status.ACTIVATED);
                    })
                    .collect(Collectors.toList());

            Single.concat(promisesForNewSessions)
                    .concatWith(Single.concat(updatesForExistingSessions))
                    .window(1)
                    .flatMapCompletable(f -> f
                            .flatMapCompletable(sessionId -> subscribe(authzToken, sessionId))
                    )
                    .doOnError(e -> {
                        LOGGER.error("Unexpected error", e);
                    })
                    .subscribe();
        });
    }

    private Single<Session> updateSession(String authzToken, Session session, Session.Status status) {
        String sessionId = session.getId();
        return client.updateSession(authzToken, sessionId, status)
                .flatMap(updateResponse -> {
                    if (updateResponse.error() != null) {
                        eventListener.onError(updateResponse.error());
                        return Single.error(new IllegalStateException(
                                "Failed to activate session "+sessionId+
                                        " for headset "+session.getHeadset().getId()+": " + updateResponse.error()));
                    } else {
                        return Single.just(updateResponse.result());
                    }
                });
    }

    private Single<Session> createSession(String authzToken, String headsetId, Session.Status status) {
        return client.createSession(authzToken, headsetId, status)
                .flatMap(createResponse -> {
                    if (createResponse.error() != null) {
                        eventListener.onError(createResponse.error());
                        return Single.error(new IllegalStateException(
                                "Failed to create session for headset "+headsetId+" with status "+status+": " +
                                        createResponse.error()));
                    } else {
                        return Single.just(createResponse.result());
                    }
                });
    }

    private Completable subscribe(String authzToken, Session session) {
        String sessionId = session.getId();
        return client.subscribe(authzToken, Collections.singleton("eeg"), sessionId, eventListener::onEvent)
                .flatMapCompletable(subscribeResponse -> {
                    if (subscribeResponse.error() != null) {
                        eventListener.onError(subscribeResponse.error());
                        return Completable.error(new IllegalStateException(
                                "Failed to subscribe to events for session "+sessionId+": " + subscribeResponse.error()));
                    } else {
                        return Completable.complete();
                    }
                });
    }

    public Completable stop() {
        // TODO
        return Completable.complete();
    }
}
