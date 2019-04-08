package net.manaty.octopusync.it.fixture.emotiv;

import net.manaty.octopusync.service.emotiv.message.Session;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CortexInfoService {

    private final Map<String, TestCortexCredentials> credentialsByUsername;
    private final ConcurrentMap<String, UserInfo> usersByClientId;
    private final ConcurrentMap<String, Session> sessionsByHeadsetId;

    public CortexInfoService(List<TestCortexCredentials> credentials, List<Session> sessions) {
        this.credentialsByUsername = collectCredentialsByUsername(credentials);
        this.usersByClientId = new ConcurrentHashMap<>();
        this.sessionsByHeadsetId = new ConcurrentHashMap<>(collectSessionsByHeadsetId(sessions));
    }

    private Map<String, Session> collectSessionsByHeadsetId(List<Session> sessions) {
        return sessions.stream().collect(Collectors.toMap(
                session -> session.getHeadset().getId(),
                Function.identity()));
    }

    private Map<String, TestCortexCredentials> collectCredentialsByUsername(List<TestCortexCredentials> credentials) {
        return credentials.stream().collect(Collectors.toMap(
                TestCortexCredentials::getUsername,
                Function.identity()));
    }

    public UserInfo getUserInfoByClientId(String clientId) {
        return usersByClientId.get(clientId);
    }

    public @Nullable UserInfo getUserInfoByAuthzToken(String authzToken) {
        return usersByClientId.values().stream()
                .filter(userInfo -> userInfo.getAuthToken() != null && userInfo.getAuthToken().equals(authzToken))
                .findFirst()
                .orElse(null);
    }

    public @Nullable TestCortexCredentials getCredentialsForUsername(String username) {
        return credentialsByUsername.get(username);
    }

    public UserInfo login(String clientId, String username) {
        // sanity check
        if (!credentialsByUsername.get(username).getClientId().equals(clientId)) {
            throw new IllegalStateException("Invalid username/client ID pair: "+username+"/"+clientId);
        }

        UserInfo userInfo = new UserInfo(username);
        if (usersByClientId.putIfAbsent(clientId, userInfo) != null) {
            throw new IllegalStateException("User info already exists for client ID: " + clientId);
        }
        return userInfo;
    }

    public List<String> getLoggedInUsers() {
        return usersByClientId.values().stream()
                .map(UserInfo::getUsername)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * @return true if the user has been logged in (and was successfully logged out), false otherwise
     */
    public boolean logout(String username) {
        boolean[] result = new boolean[1];
        usersByClientId.forEach((clientId, userInfo) -> {
            if (userInfo.getUsername().equals(username)) {
                usersByClientId.remove(clientId);
                result[0] = true;
            }
        });
        return result[0];
    }

    public List<Session> getSessions() {
        return new ArrayList<>(sessionsByHeadsetId.values());
    }
}
