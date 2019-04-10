package net.manaty.octopusync.it.fixture.emotiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.message.SubscribeResponse;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CortexEventSubscriptionService implements AutoCloseable {

    private final ObjectMapper mapper;
    private final ExecutorService executor;
    private final ConcurrentMap<String, CortexEventSubscription> subscriptionsBySessionId;

    public CortexEventSubscriptionService() {
        this.mapper = new ObjectMapper();
        this.executor = Executors.newCachedThreadPool();
        this.subscriptionsBySessionId = new ConcurrentHashMap<>();
    }

    public boolean isSubscribed(String sessionId, CortexEventKind eventKind) {
        CortexEventSubscription subscription = subscriptionsBySessionId.get(sessionId);
        return subscription != null && subscription.getEventKind().equals(eventKind);
    }

    public CortexEventSubscription subscribe(String sessionId, CortexEventKind eventKind) {
        // TODO: support other stream types?
        if (!CortexEventKind.EEG.equals(eventKind)) {
            throw new IllegalStateException("Unsupported stream type: " + eventKind.protocolValue());
        } else if (isSubscribed(sessionId, eventKind)) {
            throw new IllegalStateException("Session "+sessionId+
                    " is already subscribed to stream: "+eventKind.protocolValue());
        }

        CortexEventSubscription subscription = new CortexEventSubscription(executor, mapper, sessionId);
        subscriptionsBySessionId.put(sessionId, subscription);
        return subscription;
    }

    @Override
    public void close() {
        subscriptionsBySessionId.values().forEach(CortexEventSubscription::stop);
        executor.shutdownNow();
    }

    // TODO: support other stream types?
    public static class CortexEventSubscription implements Runnable {
        private static final Logger LOGGER = LoggerFactory.getLogger(CortexEventSubscription.class);

        private final ExecutorService executor;
        private final ObjectMapper mapper;
        private final String subscriptionId;
        private volatile Session websocketSession;
        private volatile boolean stopped;

        public CortexEventSubscription(ExecutorService executor, ObjectMapper mapper, String subscriptionId) {
            this.executor = executor;
            this.mapper = mapper;
            this.subscriptionId = Objects.requireNonNull(subscriptionId);
        }

        public CortexEventKind getEventKind() {
            return CortexEventKind.EEG;
        }

        public SubscribeResponse.StreamInfo getStreamInfo() {
            SubscribeResponse.StreamInfo streamInfo = new SubscribeResponse.StreamInfo();
            streamInfo.setStream(CortexEventKind.EEG.protocolValue());
            streamInfo.setSubscriptionId(subscriptionId);
            streamInfo.setColumns(Arrays.asList(
                    "IED_COUNTER",
                    "IED_INTERPOLATED",
                    "IED_RAW_CQ",
                    "IED_AF3",
                    "IED_T7",
                    "IED_Pz",
                    "IED_T8",
                    "IED_AF4",
                    "IED_MARKER_HARDWARE",
                    "IED_MARKER"
            ));
            return streamInfo;
        }

        public void start(Session websocketSession) {
            this.websocketSession = Objects.requireNonNull(websocketSession);
            executor.execute(this);
        }

        public void stop() {
            stopped = true;
        }

        @Override
        public void run() {
            try {
                while (!stopped) {
                    Thread.sleep(1000);

                    ObjectNode eventNode = new ObjectNode(mapper.getNodeFactory());
                    eventNode.set("sid", new TextNode(subscriptionId));
                    eventNode.set("time", new LongNode(System.currentTimeMillis()));
                    ArrayNode values = new ArrayNode(mapper.getNodeFactory());
                    values.add(1L); // counter
                    values.add(0); // interpolated
                    values.add(1.0); // raw cq
                    values.add(0.1); // af3
                    values.add(0.1); // t7
                    values.add(0.1); // pz
                    values.add(0.1); // t8
                    values.add(0.1); // af4
                    values.add(0); // hw marker
                    values.add(0); // marker
                    eventNode.set("eeg", values);

                    websocketSession.getRemote().sendString(mapper.writeValueAsString(eventNode), new WriteCallback() {
                        @Override
                        public void writeFailed(Throwable x) {
                            LOGGER.error("Failed to send event", x);
                        }

                        @Override
                        public void writeSuccess() {
                            // ignore
                        }
                    });
                }
            } catch (Exception e) {
                stopped = true;
                LOGGER.error("Unexpected error in subscription " + subscriptionId, e);
            }
        }
    }
}
