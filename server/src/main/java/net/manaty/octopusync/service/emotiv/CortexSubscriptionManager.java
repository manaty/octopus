package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.message.Headset;
import net.manaty.octopusync.service.emotiv.message.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CortexSubscriptionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexSubscriptionManager.class);

    // TODO: configurable?
    private static final Duration QUERY_HEADSETS_INTERVAL = Duration.ofSeconds(5);
    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(30);

    private final Vertx vertx;
    private final CortexClient client;
    private final String authzToken;
    private final ConcurrentMap<String, Session> existingSessionsById;
    private final Set<String> headsetIds;
    private final CortexEventListener eventListener;

    private final AtomicBoolean started;
    private volatile long timerId;
    private final Set<String> subscribedHeadsets;

    public CortexSubscriptionManager(
            Vertx vertx,
            CortexClient client,
            String authzToken,
            List<Session> existingSessions,
            Set<String> headsetIds,
            CortexEventListener eventListener) {

        this.vertx = vertx;
        this.client = client;
        this.authzToken = authzToken;
        this.existingSessionsById = collectSessionsByIdMap(existingSessions);
        this.headsetIds = headsetIds;
        this.eventListener = eventListener;
        this.started = new AtomicBoolean(false);
        this.subscribedHeadsets = ConcurrentHashMap.newKeySet();
    }

    private ConcurrentMap<String, Session> collectSessionsByIdMap(List<Session> existingSessions) {
        return existingSessions.stream()
                .collect(Collectors.toMap(
                        Session::getId,
                        Function.identity(),
                        (s1, s2) -> { throw new IllegalStateException(); },
                        ConcurrentHashMap::new));
    }

    public Completable start() {
        return Completable.fromAction(() -> {
            if (started.compareAndSet(false, true)) {
                scheduleQueryingHeadsets();
            }
        });
    }

    private void scheduleQueryingHeadsets() {
        scheduleQueryingHeadsets(1, QUERY_HEADSETS_INTERVAL.toMillis());
    }

    private void scheduleQueryingHeadsets(long delayMillis, long nextDelayMillis) {
        timerId = vertx.setTimer(delayMillis, it -> {
            client.queryHeadsets()
                    .flatMapCompletable(r -> processHeadsets(r.result()))
                    .doAfterTerminate(() -> scheduleQueryingHeadsets(nextDelayMillis, nextDelayMillis))
                    .subscribe(() -> {}, e -> {
                        LOGGER.error("Failed to query headsets", e);
                    });
        });
    }

    private Completable processHeadsets(List<Headset> headsets) {
        return Observable.fromIterable(headsets)
                .filter(headset -> {
                    String headsetId = headset.getId();
                    boolean knownHeadset = headsetIds.contains(headsetId);
                    if (!knownHeadset) {
                        LOGGER.info("Skipping unknown headset: {}", headsetId);
                    }
                    return knownHeadset;
                })
                .doOnNext(headset -> {
                    if (subscribedHeadsets.add(headset.getId())) {
                        subscribe(headset);
                    }
                })
                .ignoreElements();
    }

    private void subscribe(Headset headset) {
        Single.defer(() -> {
            String headsetId = headset.getId();

            List<Session> sessions = existingSessionsById.values().stream()
                    // filter out sessions, created by Emotiv software, closed sessions, and sessions for unknown headsets
//                    .filter(session -> !session.getAppId().equalsIgnoreCase("com.emotiv.emotivpro"))
                    .filter(session -> !Session.hasStatus(session, Session.Status.CLOSED))
                    .filter(session -> headset.getId().equals(session.getHeadset().getId()))
                    .sorted(Session.comparator)
                    .collect(Collectors.toList());

            if (sessions.isEmpty()) {
                LOGGER.info("No valid session exists for headset {}, will create and activate...", headsetId);
                return createSession(authzToken, headsetId, Session.Status.ACTIVE);
            } else {
                // get last session
                Session session = sessions.get(sessions.size() - 1);

                String sessionId = session.getId();

                Session.Status status = Session.getStatus(session);
                switch (status) {
                    case OPENED: {
                        LOGGER.info("Session {} for headset {} has '{}' status, will activate before subscribing...",
                                sessionId, headsetId, status);
                        return updateSession(authzToken, session, Session.Status.ACTIVE);
                    }
                    case ACTIVE:
                    case ACTIVATED: {
                        LOGGER.info("Session {} for headset {} is already active, will subscribe immediately...",
                                session.getId(), session.getHeadset().getId());
                        return Single.just(session);
                    }
                    default: {
                        throw new IllegalStateException("Unknown session status: " + status);
                    }
                }
            }
        }).flatMapCompletable(session -> subscribe(authzToken, session))
                .subscribe();
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
                }).doOnSuccess(session -> {
                    existingSessionsById.put(session.getId(), session);
                });
    }

    private Completable subscribe(String authzToken, Session session) {
        String sessionId = session.getId();
        return client.subscribe(authzToken, Collections.singleton(CortexEventKind.EEG), sessionId, eventListener)
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
        return Completable.fromAction(() -> {
            if (started.compareAndSet(true, false)) {
                vertx.cancelTimer(timerId);
            }
        });
    }

    public void onSessionStopped(String sessionId) {
        Session session = existingSessionsById.remove(sessionId);
        if (session == null) {
            LOGGER.warn("Unknown session {}", sessionId);
        } else {
            String headsetId = session.getHeadset().getId();
            subscribedHeadsets.remove(headsetId);
        }
    }
}
