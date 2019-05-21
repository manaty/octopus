package net.manaty.octopusync.service.web.ws;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.service.web.ws.message.ClientListMessage;
import net.manaty.octopusync.service.web.ws.message.ClientStateMessage;
import net.manaty.octopusync.service.web.ws.message.HeadsetListMessage;
import net.manaty.octopusync.service.web.ws.message.ServerListMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ServerEndpoint(
        value = "/admin",
        encoders = {
                ServerListMessage.Encoder.class,
                ClientListMessage.Encoder.class,
                ClientStateMessage.Encoder.class,
                HeadsetListMessage.Encoder.class,
        })
@SuppressWarnings("unused")
public class AdminEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminEndpoint.class);

    public static final String JSON_MAPPER_PROPERTY = "admin.jsonmapper";

    private final ObjectMapper mapper;
    private final ConcurrentMap<String, Session> sessionsById;

    private final Duration reportingInterval;
    private final AtomicReference<ExecutorService> executor;
    private final AtomicLong messageCounter;

    //---------------------------------------- State ----------------------------------------//
    private final ConcurrentMap<String, S2STimeSyncResult> lastS2SSyncResultByAddress;
    private final ConcurrentMap<String, ClientTimeSyncResult> lastClientSyncResultByHeadsetId;
    private final ConcurrentMap<String, MoodState> lastClientStateByHeadsetId;
    private volatile Set<String> allKnownHeadsets;
    private final Set<String> headsetIdsWithActiveClientSession;
    private volatile Set<String> connectedHeadsets;
    //---------------------------------------------------------------------------------------//

    public AdminEndpoint(Duration reportingInterval) {
        this.mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.sessionsById = new ConcurrentHashMap<>();
        this.reportingInterval = reportingInterval;
        this.executor = new AtomicReference<>(null);
        this.messageCounter = new AtomicLong(1);
        this.lastS2SSyncResultByAddress = new ConcurrentHashMap<>();
        this.lastClientSyncResultByHeadsetId = new ConcurrentHashMap<>();
        this.lastClientStateByHeadsetId = new ConcurrentHashMap<>();
        this.allKnownHeadsets = Collections.emptySet();
        this.headsetIdsWithActiveClientSession = ConcurrentHashMap.newKeySet();
        this.connectedHeadsets = Collections.emptySet();
    }

    public synchronized void init() {
        if (this.executor.get() != null) {
            throw new IllegalStateException("Already started");
        }

        ScheduledExecutorService executor = Executors
                .newScheduledThreadPool(1, r -> new Thread(r, "admin-endpoint-executor"));
        executor.scheduleWithFixedDelay(this::report,
                reportingInterval.toMillis(), reportingInterval.toMillis(), TimeUnit.MILLISECONDS);
        this.executor.set(executor);
    }

    private void report() {
        try {
            Map<String, List<ServerListMessage.SyncResult>> s2sSyncResults = lastS2SSyncResultByAddress.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                        S2STimeSyncResult syncResult = e.getValue();
                        if (syncResult == null) {
                            return Collections.emptyList();
                        } else {
                            return Collections.singletonList(new ServerListMessage.SyncResult(
                                    syncResult.getFinished(), syncResult.getDelay(), syncResult.getError()));
                        }
                    }));

            Map<String, List<ClientListMessage.SyncResult>> clientSyncResults = lastClientSyncResultByHeadsetId.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                        ClientTimeSyncResult syncResult = e.getValue();
                        if (syncResult == null) {
                            return Collections.emptyList();
                        } else {
                            return Collections.singletonList(new ClientListMessage.SyncResult(
                                    syncResult.getFinished(), syncResult.getDelay(), syncResult.getError()));
                        }
                    }));

            Map<String, List<ClientStateMessage.State>> clientStates = lastClientStateByHeadsetId.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                        MoodState moodState = e.getValue();
                        if (moodState == null) {
                            return Collections.emptyList();
                        } else {
                            return Collections.singletonList(new ClientStateMessage.State(
                                    moodState.getState(), moodState.getSinceTimeUtc()));
                        }
                    }));

            Map<String, HeadsetListMessage.Status> statuses = new HashMap<>((int)(allKnownHeadsets.size() / 0.75d + 1));
            allKnownHeadsets.forEach(headsetId -> {
                HeadsetListMessage.Status status = new HeadsetListMessage.Status(
                        connectedHeadsets.contains(headsetId),
                        headsetIdsWithActiveClientSession.contains(headsetId));
                statuses.put(headsetId, status);
            });

            ServerListMessage serverListMessage = new ServerListMessage(messageCounter.getAndIncrement(), s2sSyncResults);
            ClientListMessage clientListMessage = new ClientListMessage(messageCounter.getAndIncrement(), clientSyncResults);
            ClientStateMessage clientStateMessage = new ClientStateMessage(messageCounter.getAndIncrement(), clientStates);
            HeadsetListMessage headsetListMessage = new HeadsetListMessage(messageCounter.getAndIncrement(), statuses);

            sessionsById.forEach((id, session) -> {
                send(session, serverListMessage);
                send(session, clientListMessage);
                send(session, clientStateMessage);
                send(session, headsetListMessage);
            });
        } catch (Exception e) {
            LOGGER.error("Reporting failed, will retry after the configured delay" +
                    " (" + reportingInterval.toMillis() + " ms)", e);
        }
    }

    private void send(Session session, Object message) {
        try {
            session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            LOGGER.error("Failed to send message " + message + " to session " + session.getId(), e);
        }
    }

    public void shutdown() {
        ExecutorService executor = this.executor.getAndSet(null);
        if (executor != null) {
            executor.shutdownNow();
            try {
                while (!executor.isTerminated()) {
                    executor.awaitTermination(1, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("Unexpectedly interrupted");
            }
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        session.getUserProperties().put(JSON_MAPPER_PROPERTY, mapper);

        if (sessionsById.put(session.getId(), session) == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Session {} opened", session.getId());
            }
        } else {
            LOGGER.warn("Session {} opened for the second time..." +
                    " Is the session properly removed in onClose()?");
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        if (sessionsById.remove(session.getId()) != null) {
            if (LOGGER.isDebugEnabled()) {
                String reasonPhrase = reason.getReasonPhrase();
                if (reasonPhrase == null) {
                    LOGGER.debug("Session {} closed", session.getId());
                } else {
                    LOGGER.debug("Session {} closed, reason: {}", session.getId(), reasonPhrase);
                }

            }
        } else {
            LOGGER.warn("Session {} closed without being opened..." +
                    " Is the session properly registered in onOpen()?");
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        LOGGER.error("Session " + session.getId() + " closed due to error", e);

        if (sessionsById.remove(session.getId()) == null) {
            LOGGER.warn("Session {} closed due to error without being opened..." +
                    " Is the session properly registered in onOpen()?");
        }
    }

    public void onS2STimeSyncResult(S2STimeSyncResult r) {
        lastS2SSyncResultByAddress.put(r.getRemoteAddress(), r);
    }

    public void onClientTimeSyncResult(ClientTimeSyncResult r) {
        lastClientSyncResultByHeadsetId.put(r.getHeadsetId(), r);
    }

    public void onClientSessionCreated(String headsetId) {
        headsetIdsWithActiveClientSession.add(headsetId);
    }

    public void onClientStateUpdate(MoodState moodState) {
        lastClientStateByHeadsetId.put(moodState.getHeadsetId(), moodState);
    }

    public void onKnownHeadsetsUpdated(Set<String> headsetIds) {
        this.allKnownHeadsets = headsetIds;
    }

    public void onConnectedHeadsetsUpdated(Set<String> headsetIds) {
        this.connectedHeadsets = headsetIds;
    }
}
