package net.manaty.octopusync.di;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.NetworkUtils;
import net.manaty.octopusync.service.emotiv.EmotivCredentials;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@BQConfig("Contains configuration related to capturing events from Emotiv Cortex server.")
public class CortexConfiguration {

    private static final Duration DEFAULT_REFRESH_HEADSETS_INTERVAL = Duration.ofSeconds(3);
    private static final Duration DEFAULT_HEADSET_INACTIVITY_THRESHOLD = Duration.ofSeconds(3);
    private static final Duration DEFAULT_SUBSCRIPTION_RETRY_INTERVAL = Duration.ofSeconds(15);

    private String cortexServerAddress;
    private boolean useSsl;
    private Map<String, String> headsetIdsToCodes;
    private EmotivConfiguration emotivConfiguration;
    private long refreshHeadsetsIntervalMillis;
    private long headsetInactivityThresholdMillis;
    private long subscriptionRetryIntervalMillis;

    public CortexConfiguration() {
        this.useSsl = true;
        this.headsetIdsToCodes = Collections.emptyMap();
        this.refreshHeadsetsIntervalMillis = DEFAULT_REFRESH_HEADSETS_INTERVAL.toMillis();
        this.headsetInactivityThresholdMillis = DEFAULT_HEADSET_INACTIVITY_THRESHOLD.toMillis();
        this.subscriptionRetryIntervalMillis = DEFAULT_SUBSCRIPTION_RETRY_INTERVAL.toMillis();
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

    @BQConfigProperty("Period, at which querying Cortex server for connected headsets will be performed.")
    public void setRefreshHeadsetsIntervalMillis(long refreshHeadsetsIntervalMillis) {
        if (refreshHeadsetsIntervalMillis <= 0) {
            throw new IllegalArgumentException("Invalid refresh headsets interval (millis): " + refreshHeadsetsIntervalMillis);
        }
        this.refreshHeadsetsIntervalMillis = refreshHeadsetsIntervalMillis;
    }

    @BQConfigProperty("Period of not receiving any events, after which a headset " +
            "(reported as connected by Cortex server) will be considered disconnected.")
    public void setHeadsetInactivityThresholdMillis(long headsetInactivityThresholdMillis) {
        if (headsetInactivityThresholdMillis <= 0) {
            throw new IllegalArgumentException("Invalid headset inactivity threshold (millis): " + headsetInactivityThresholdMillis);
        }
        this.headsetInactivityThresholdMillis = headsetInactivityThresholdMillis;
    }

    @BQConfigProperty("Period, after which an attempt to resubscribe to a terminated headset session will be performed.")
    public void setSubscriptionRetryIntervalMillis(long subscriptionRetryIntervalMillis) {
        if (subscriptionRetryIntervalMillis <= 0) {
            throw new IllegalArgumentException("Invalid subscription retry interval (millis): " + subscriptionRetryIntervalMillis);
        }
        this.subscriptionRetryIntervalMillis = subscriptionRetryIntervalMillis;
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

    public Duration getRefreshHeadsetsInterval() {
        return Duration.ofMillis(refreshHeadsetsIntervalMillis);
    }

    public Duration getHeadsetInactivityThreshold() {
        return Duration.ofMillis(headsetInactivityThresholdMillis);
    }

    public Duration getSubscriptionRetryInterval() {
        return Duration.ofMillis(subscriptionRetryIntervalMillis);
    }
}
