package net.manaty.octopusync.service.emotiv;

import java.util.Objects;

public class EmotivCredentials {

    private String username;
    private String password;
    private String clientId;
    private String clientSecret;

    public EmotivCredentials(String username, String password, String clientId, String clientSecret) {
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.clientId = Objects.requireNonNull(clientId);
        this.clientSecret = Objects.requireNonNull(clientSecret);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
