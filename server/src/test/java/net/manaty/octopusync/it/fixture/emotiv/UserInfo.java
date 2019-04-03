package net.manaty.octopusync.it.fixture.emotiv;

import java.util.Objects;

public class UserInfo {
    private final String username;
    private volatile String authToken;

    public UserInfo(String username) {
        this.username = Objects.requireNonNull(username);
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
