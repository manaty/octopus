package net.manaty.octopusync.di;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;

@BQConfig
public class EmotivConfiguration {

    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String license;

    @BQConfigProperty
    public void setUsername(String username) {
        this.username = username;
    }

    @BQConfigProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @BQConfigProperty
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @BQConfigProperty
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @BQConfigProperty
    public void setLicense(String license) {
        this.license = license;
    }

    public EmotivCredentials createCredentials() {
        if (username == null || username.isEmpty()) {
            throw new IllegalStateException("Missing Emotiv username");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("Missing Emotiv password");
        }
        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalStateException("Missing Emotiv client ID");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new IllegalStateException("Missing Emotiv client secret");
        }
        return new EmotivCredentials(username, password, clientId, clientSecret, license);
    }
}
