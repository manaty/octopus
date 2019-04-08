package net.manaty.octopusync.it.fixture.emotiv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.manaty.octopusync.service.emotiv.message.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestCortexResources {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCortexResources.class);

    public static List<TestCortexCredentials> loadCredentials() {
        URL credentialsFile = Objects.requireNonNull(TestCortexServerModule.class.getResource("/cortex/credentials.json"));
        List<TestCortexCredentials> credentials;
        try {
            credentials = new ObjectMapper().readValue(credentialsFile, new TypeReference<List<TestCortexCredentials>>(){});
        } catch (Exception e) {
            String message = "Failed to read test credentials from file";
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
        return credentials;
    }

    public static List<Session> loadSessions() {
        URL sessionsFile = Objects.requireNonNull(TestCortexServerModule.class.getResource("/cortex/sessions.json"));
        List<Session> sessions;
        try {
            sessions = new ObjectMapper().readValue(sessionsFile, new TypeReference<List<Session>>(){});
        } catch (Exception e) {
            String message = "Failed to read test sessions from file";
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
        return sessions;
    }

    public static Map<String, String> loadHeadsetIdsToCodes() {
        URL headsetsFile = Objects.requireNonNull(TestCortexServerModule.class.getResource("/cortex/headsets.json"));
        ArrayNode nodeList;
        try {
            nodeList = (ArrayNode) new ObjectMapper().readTree(headsetsFile);
        } catch (Exception e) {
            String message = "Failed to read test headsets from file";
            LOGGER.error(message, e);
            throw new RuntimeException(message, e);
        }
        Map<String, String> headsetIdsToCodes = new HashMap<>();
        nodeList.forEach(node -> {
            String id = Objects.requireNonNull(node.get("id").textValue());
            String code = Objects.requireNonNull(node.get("code").textValue());
            headsetIdsToCodes.put(id, code);
        });
        return headsetIdsToCodes;
    }
}
