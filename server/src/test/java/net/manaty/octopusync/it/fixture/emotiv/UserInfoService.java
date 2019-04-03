package net.manaty.octopusync.it.fixture.emotiv;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class UserInfoService {

    private final ConcurrentMap<String, UserInfo> usersByClientId;

    public UserInfoService() {
        this.usersByClientId = new ConcurrentHashMap<>();
    }

    public UserInfo getUserInfoByClientId(String clientId) {
        return usersByClientId.get(clientId);
    }

    public UserInfo createUserInfoForClientId(String clientId, String username) {
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
}
