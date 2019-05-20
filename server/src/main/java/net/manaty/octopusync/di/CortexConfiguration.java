package net.manaty.octopusync.di;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.NetworkUtils;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;

@BQConfig("Contains configuration related to capturing events from Emotiv Cortex server.")
public class CortexConfiguration {

    private String cortexServerAddress;
    private boolean useSsl;
    private Map<String, String> headsetIdsToCodes;
    private EmotivConfiguration emotivConfiguration;

    public CortexConfiguration() {
        this.useSsl = true;
        this.headsetIdsToCodes = Collections.emptyMap();
    }

    @BQConfigProperty("Cortex server address in format <host>:<port>." +
            " Note that apparently Cortex WebSocket API expects the Host request header" +
            " to be 'emotivcortex.com', regardless of the actual host, on which the Cortex server is running." +
            " Hence, the <host> part in this parameter's value must always be 'emotivcortex.com'," +
            " and hosts configuration file in your OS (e.g. /etc/hosts) must contain a mapping from" +
            " emotivcortex.com to the actual IP address of the server.")
    public void setCortexServerAddress(String cortexServerAddress) {
        this.cortexServerAddress = cortexServerAddress;
    }

    @BQConfigProperty("Whether Cortex WebSocket client should use HTTPS connections." +
            " Note that when setting this to true, the Cortex server's host's certificate" +
            " must be installed in OctopuSync's JVM.")
    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    @BQConfigProperty("Mapping of headset IDs to human readable codes.")
    public void setHeadsetIdsToCodes(Map<String, String> headsetIdsToCodes) {
        this.headsetIdsToCodes = headsetIdsToCodes;
    }

    @BQConfigProperty
    public void setEmotiv(EmotivConfiguration emotivConfiguration) {
        this.emotivConfiguration = emotivConfiguration;
    }

    public InetSocketAddress resolveCortexServerAddress() {
        if (cortexServerAddress == null || cortexServerAddress.isEmpty()) {
            throw new IllegalStateException("Missing Cortex server address");
        }
        return NetworkUtils.parseAddress(cortexServerAddress);
    }

    public Map<String, String> getHeadsetIdsToCodes() {
        return headsetIdsToCodes;
    }

    public EmotivCredentials getEmotivCredentials() {
        if (emotivConfiguration == null) {
            throw new IllegalStateException("Emotiv configuration is missing");
        }
        return emotivConfiguration.createCredentials();
    }

    public boolean shouldUseSsl() {
        return useSsl;
    }
}
