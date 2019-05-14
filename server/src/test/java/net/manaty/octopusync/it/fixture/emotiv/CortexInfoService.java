package net.manaty.octopusync.it.fixture.emotiv;

import net.manaty.octopusync.service.emotiv.message.Headset;
import net.manaty.octopusync.service.emotiv.message.Session;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CortexInfoService {

    private final Map<String, TestCortexCredentials> credentialsByUsername;
    private final ConcurrentMap<String, UserInfo> usersByClientId;
    private final ConcurrentMap<String, Session> sessionsByHeadsetId;
    private final Set<String> headsetIds;

    public CortexInfoService(List<TestCortexCredentials> credentials, List<Session> sessions, Set<String> headsetIds) {
        this.credentialsByUsername = collectCredentialsByUsername(credentials);
        this.usersByClientId = new ConcurrentHashMap<>();
        this.sessionsByHeadsetId = new ConcurrentHashMap<>(collectSessionsByHeadsetId(sessions));
        this.headsetIds = Collections.unmodifiableSet(headsetIds);
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

    public Session createSession(String authzToken, String headsetId, Session.Status status) {
        UserInfo userInfo = Objects.requireNonNull(getUserInfoByAuthzToken(authzToken),
                "Unknown authz token: " + authzToken);

        Session session = sessionsByHeadsetId.get(headsetId);
        if (session != null && !Session.Status.CLOSED.protocolValue().equals(session.getStatus())) {
            throw new IllegalStateException("Session for headset ID "+headsetId+" already exists");
        }

        Session newSession = new Session();
        newSession.setId(UUID.randomUUID().toString());
        newSession.setStatus(status.protocolValue());
        newSession.setOwner(userInfo.getUsername());
        Headset headset = new Headset();
        headset.setId(headsetId);
        newSession.setHeadset(headset);

        sessionsByHeadsetId.put(headsetId, newSession);

        return newSession;
    }

    public Session updateSession(String authzToken, String sessionId, Session.Status status) {
        UserInfo userInfo = Objects.requireNonNull(getUserInfoByAuthzToken(authzToken),
                "Unknown authz token: " + authzToken);

        Session session = getSessions().stream()
                .filter(s -> s.getId().equals(sessionId))
                .findAny().orElseThrow(() -> new IllegalStateException("Unknown session ID: " + sessionId));

        if (!session.getOwner().equals(userInfo.getUsername())) {
            throw new IllegalStateException("Session was created by different username: " + session.getOwner());
        }

        session.setStatus(status.protocolValue());
        return session;
    }

    public Set<String> getHeadsetIds() {
        return headsetIds;
    }
}
