package net.manaty.octopusync.service.emotiv;

import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import java.util.Objects;

public class EmotivCredentials {

    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;
    private final String appId;
    private final String license;

    public EmotivCredentials(
            String username, String password, String clientId, String clientSecret, String appId, @Nullable String license) {

        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.clientId = Objects.requireNonNull(clientId);
        this.clientSecret = Objects.requireNonNull(clientSecret);
        this.appId = Objects.requireNonNull(appId);
        this.license = license;
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

    public String getAppId() {
        return appId;
    }

    public String getLicense() {
        return license;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("password", password)
                .add("clientId", clientId)
                .add("clientSecret", clientSecret)
                .add("appId", appId)
                .add("license", license)
                .toString();
    }
}
