package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.model.DevEvent;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MotEvent;
import net.manaty.octopusync.service.EventListener;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.event.CortexEventVisitor;
import net.manaty.octopusync.service.emotiv.message.Headset;
import net.manaty.octopusync.service.emotiv.message.Response;
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

//    private static final Duration REFRESH_HEADSETS_INTERVAL_DEFAULT = Duration.ofSeconds(5);
//    // TODO: configurable?
//    private static final Duration RETRY_INTERVAL = Duration.ofSeconds(30);

    private final Vertx vertx;
    private final CortexClient client;
    private final String authzToken;
    private final ConcurrentMap<String, Session> existingSessionsById;
    private final Set<String> headsetIds;
    private final CortexEventListener cortexEventListener;
    private final Set<EventListener> eventListeners;
    private final Duration refreshHeadsetsInterval;
    private final Duration headsetInactivityThreshold;
    private final Duration subscriptionRetryInterval;

    private final AtomicBoolean started;
    private volatile long timerId;
    private final Set<String> subscribedHeadsets;

    private final ConcurrentMap<String, Long> lastEventReceivedTimeByHeadsetId;

    public CortexSubscriptionManager(
            Vertx vertx,
            CortexClient client,
            String authzToken,
            List<Session> existingSessions,
            Set<String> headsetIds,
            CortexEventListener cortexEventListener,
            Set<EventListener> eventListeners,
            Duration refreshHeadsetsInterval,
            Duration headsetInactivityThreshold,
            Duration subscriptionRetryInterval) {

        this.vertx = vertx;
        this.client = client;
        this.authzToken = authzToken;
        this.existingSessionsById = collectSessionsByIdMap(existingSessions);
        this.headsetIds = headsetIds;
        this.cortexEventListener = cortexEventListener;
        this.eventListeners = eventListeners;
        this.refreshHeadsetsInterval = refreshHeadsetsInterval;
        this.headsetInactivityThreshold = headsetInactivityThreshold;
        this.subscriptionRetryInterval = subscriptionRetryInterval;
        this.started = new AtomicBoolean(false);
        this.subscribedHeadsets = ConcurrentHashMap.newKeySet();
        this.lastEventReceivedTimeByHeadsetId = new ConcurrentHashMap<>();
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
        scheduleQueryingHeadsets(1, refreshHeadsetsInterval.toMillis());
    }

    private void scheduleQueryingHeadsets(long delayMillis, long nextDelayMillis) {
        timerId = vertx.setTimer(delayMillis, it -> {
            client.queryHeadsets()
                    .doOnSuccess(r -> {
                        long currentTime = System.currentTimeMillis();
                        Set<String> connectedHeadsetIds = r.result().stream()
                                .map(Headset::getId)
                                .filter(id -> {
                                    Long lastEventReceivedTime = lastEventReceivedTimeByHeadsetId.get(id);
                                    // for new, just connected headsets this value will be absent
                                    // until the first event has been received
                                    if (lastEventReceivedTime == null) {
                                        return true;
                                    }
                                    long inactiveMillis = currentTime - lastEventReceivedTime;
                                    if (inactiveMillis < headsetInactivityThreshold.toMillis()) {
                                        return true;
                                    } else if (LOGGER.isDebugEnabled()) {
                                        LOGGER.debug("Headset {} has been inactive for {} seconds",
                                                id, Duration.ofMillis(inactiveMillis).getSeconds());
                                    }
                                    return false;
                                })
                                .collect(Collectors.toSet());
                        eventListeners.forEach(l -> l.onConnectedHeadsetsUpdated(connectedHeadsetIds));
                    })
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
                                LOGGER.error(errorMessage + "; will retry in " + subscriptionRetryInterval.toMillis() + " ms");
                                return updateSession(authzToken, session, status)
                                        .delaySubscription(subscriptionRetryInterval.toMillis(), TimeUnit.MILLISECONDS);
                            }
                            default: {
                                cortexEventListener.onError(updateResponse.error());
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
                                LOGGER.error(errorMessage + "; will retry in " + subscriptionRetryInterval.toMillis() + " ms");
                                return createSession(authzToken, headsetId, status)
                                        .delaySubscription(subscriptionRetryInterval.toMillis(), TimeUnit.MILLISECONDS);
                            }
                            default: {
                                cortexEventListener.onError(createResponse.error());
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
        CortexEventListener decoratedListener = new HeadsetUpdatingListener(session.getHeadset().getId(), cortexEventListener);
        Set<CortexEventKind> streams = new HashSet<>(Arrays.asList(CortexEventKind.EEG, CortexEventKind.DEV, CortexEventKind.MOT));
        return client.subscribe(authzToken, streams, sessionId, decoratedListener)
                .flatMapCompletable(subscribeResponse -> {
                    if (subscribeResponse.error() != null) {
                        String errorMessage = "Failed to subscribe to events for session "+sessionId+": " + subscribeResponse.error();
                        switch (ResponseErrors.byCode(subscribeResponse.error().getCode())) {
                            case NO_HEADSET_CONNECTED:
                            case HEADSET_DISCONNECTED: {
                                LOGGER.error(errorMessage + "; will retry in " + subscriptionRetryInterval.toMillis() + " ms");
                                return subscribe(authzToken, session)
                                        .delaySubscription(subscriptionRetryInterval.toMillis(), TimeUnit.MILLISECONDS);
                            }
                            default: {
                                cortexEventListener.onError(subscribeResponse.error());
                                return Completable.error(new IllegalStateException(errorMessage));
                            }
                        }
                    } else {
                        return Completable.complete();
                    }
                });
    }

    private class HeadsetUpdatingListener implements CortexEventListener {

        private final String headsetId;
        private final CortexEventVisitor visitor;
        private final CortexEventListener delegate;

        private HeadsetUpdatingListener(String headsetId, CortexEventListener delegate) {
            this.headsetId = headsetId;
            this.visitor = new CortexEventVisitor() {
                @Override
                public void visitEegEvent(EegEvent event) {
                    event.setHeadsetId(headsetId);
                }
                @Override
                public void visitDevEvent(DevEvent event) {
                    event.setHeadsetId(headsetId);
                }

                @Override
                public void visitMotEvent(MotEvent event) {
                    event.setHeadsetId(headsetId);
                }
            };
            this.delegate = delegate;
        }

        @Override
        public void onEvent(CortexEvent event) {
            lastEventReceivedTimeByHeadsetId.put(headsetId, System.currentTimeMillis());
            event.visitEvent(visitor);
            delegate.onEvent(event);
        }

        @Override
        public void onError(Response.ResponseError error) {
            delegate.onError(error);
        }

        @Override
        public void onError(Throwable e) {
            delegate.onError(e);
        }

        @Override
        public void onSessionStopped(String sessionId) {
            delegate.onSessionStopped(sessionId);
        }
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
