package net.manaty.octopusync.di;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;

@BQConfig("Emotiv Cloud credentials.")
public class EmotivConfiguration {

    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String appId;
    private String license;

    @BQConfigProperty
    public void setUsername(String username) {
        this.username = username;
    }

    @BQConfigProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @BQConfigProperty("Client ID; can be retrieved from https://www.emotiv.com/ account page.")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @BQConfigProperty("Client secret; can be retrieved from https://www.emotiv.com/ account page.")
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @BQConfigProperty("Application ID, for which the client ID/secret were issued.")
    public void setAppId(String appId) {
        this.appId = appId;
    }

    @BQConfigProperty("License key.")
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
        if (appId == null || appId.isEmpty()) {
            throw new IllegalStateException("Missing Emotiv application ID");
        }
        return new EmotivCredentials(username, password, clientId, clientSecret, appId, license);
    }
}
