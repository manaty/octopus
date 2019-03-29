package net.manaty.octopusync.di;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.NetworkUtils;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;

@BQConfig
public class CortexConfiguration {

    private String cortexServerAddress;
    private Map<String, String> headsetIdsToCodes;
    private EmotivConfiguration emotivConfiguration;

    public CortexConfiguration() {
        this.headsetIdsToCodes = Collections.emptyMap();
    }

    @BQConfigProperty
    public void setCortexServerAddress(String cortexServerAddress) {
        this.cortexServerAddress = cortexServerAddress;
    }

    @BQConfigProperty("Mapping of headset IDs to human readable codes")
    public void setHeadsetIdsToCodes(Map<String, String> headsetIdsToCodes) {
        this.headsetIdsToCodes = headsetIdsToCodes;
    }

    @BQConfigProperty("Emotiv Cloud credentials")
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
}
