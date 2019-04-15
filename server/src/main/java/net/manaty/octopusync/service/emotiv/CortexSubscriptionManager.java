package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Single;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.message.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CortexSubscriptionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexSubscriptionManager.class);

    // TODO: configurable?
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(30);

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

            Map<String, Session> lastSessionsForKnownHeadsets = new HashMap<>();
            for (Session session : existingSessions) {
                String headsetId = session.getHeadset().getId();
                if (headsetIds.contains(headsetId)) {
                    Session existingSession = lastSessionsForKnownHeadsets.get(headsetId);
                    boolean currentSessionClosed = Session.hasStatus(session, Session.Status.CLOSED);
                    if (currentSessionClosed) {
                        LOGGER.info("Skipping closed session {} for headset {} (status is '{}')",
                                session.getId(), headsetId, session.getStatus());
                    } else if (existingSession == null) {
                        lastSessionsForKnownHeadsets.put(headsetId, session);
                    } else if (Session.comparator.compare(session, existingSession) > 0) {
                        LOGGER.info("Will subscribe to session {} for headset {}," +
                                        " as it has preferred status or more recent start time than session {}",
                                session.getId(), headsetId, existingSession.getId());
                        lastSessionsForKnownHeadsets.put(headsetId, session);
                    }
                }
            }

            headsetIds.removeAll(lastSessionsForKnownHeadsets.keySet());

            List<Single<Session>> updatesForExistingSessions = lastSessionsForKnownHeadsets.values().stream()
                    .map(session -> {
                        String sessionId = session.getId();
                        String headsetId = session.getHeadset().getId();
                        Session.Status status = Session.getStatus(session);
                        switch (status) {
                            case CLOSED:
                            case OPENED: {
                                LOGGER.info("Session {} for headset {} has '{}' status, will activate...",
                                        sessionId, headsetId, status);
                                return updateSession(authzToken, session, Session.Status.ACTIVE);
                            }
                            case ACTIVE:
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
                        return createSession(authzToken, headsetId, Session.Status.ACTIVE);
                    })
                    .collect(Collectors.toList());

            Single.merge(promisesForNewSessions)
                    .mergeWith(Single.merge(updatesForExistingSessions))
                    .flatMap(session -> subscribe(authzToken, session).toFlowable())
                    .subscribe(it -> {}, e -> LOGGER.error("Unexpected error", e));
        });
    }

    private Single<Session> updateSession(String authzToken, Session session, Session.Status status) {
        String sessionId = session.getId();
        return client.updateSession(authzToken, sessionId, status)
                .flatMap(updateResponse -> {
                    if (updateResponse.error() != null) {
                        String errorMessage = "Failed to activate session "+sessionId+
                                " for headset "+session.getHeadset().getId()+": " + updateResponse.error();
                        switch (ResponseErrors.byCode(updateResponse.error().getCode())) {
                            case NO_HEADSET_CONNECTED:
                            case HEADSET_DISCONNECTED: {
                                LOGGER.error(errorMessage + "; will retry in " + RETRY_INTERVAL.toMillis() + " ms");
                                return updateSession(authzToken, session, status)
                                        .delaySubscription(RETRY_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
                            }
                            default: {
                                eventListener.onError(updateResponse.error());
                                return Single.error(new IllegalStateException(errorMessage));
                            }
                        }
                    } else {
                        return Single.just(updateResponse.result());
                    }
                });
    }

    private Single<Session> createSession(String authzToken, String headsetId, Session.Status status) {
        return client.createSession(authzToken, headsetId, status)
                .flatMap(createResponse -> {
                    if (createResponse.error() != null) {
                        String errorMessage = "Failed to create session for headset "+headsetId+
                                " with status "+status+": " + createResponse.error();
                        switch (ResponseErrors.byCode(createResponse.error().getCode())) {
                            case NO_HEADSET_CONNECTED:
                            case HEADSET_DISCONNECTED: {
                                LOGGER.error(errorMessage + "; will retry in " + RETRY_INTERVAL.toMillis() + " ms");
                                return createSession(authzToken, headsetId, status)
                                        .delaySubscription(RETRY_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
                            }
                            default: {
                                eventListener.onError(createResponse.error());
                                return Single.error(new IllegalStateException(errorMessage));
                            }
                        }
                    } else {
                        return Single.just(createResponse.result());
                    }
                });
    }

    private Completable subscribe(String authzToken, Session session) {
        String sessionId = session.getId();
        return client.subscribe(authzToken, Collections.singleton(CortexEventKind.EEG), sessionId, eventListener::onEvent)
                .flatMapCompletable(subscribeResponse -> {
                    if (subscribeResponse.error() != null) {
                        String errorMessage = "Failed to subscribe to events for session "+sessionId+": " + subscribeResponse.error();
                        switch (ResponseErrors.byCode(subscribeResponse.error().getCode())) {
                            case NO_HEADSET_CONNECTED:
                            case HEADSET_DISCONNECTED: {
                                LOGGER.error(errorMessage + "; will retry in " + RETRY_INTERVAL.toMillis() + " ms");
                                return subscribe(authzToken, session)
                                        .delaySubscription(RETRY_INTERVAL.toMillis(), TimeUnit.MILLISECONDS);
                            }
                            default: {
                                eventListener.onError(subscribeResponse.error());
                                return Completable.error(new IllegalStateException(errorMessage));
                            }
                        }
                    } else {
                        return Completable.complete();
                    }
                });
    }

    public Completable stop() {
        return Completable.complete();
    }
}
