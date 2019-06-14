package net.manaty.octopusync.di;

import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.NetworkUtils;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@BQConfig("Contains configuration related to OctopuSync gRPC API.")
public class GrpcConfiguration {

    private static final Duration DEFAULT_MASTER_LOOKUP_INTERVAL = Duration.ofSeconds(5);
    private static final Duration DEFAULT_MASTER_SYNC_INTERVAL = Duration.ofMinutes(1);
    private static final Duration DEFAULT_CLIENT_SYNC_INTERVAL = Duration.ofMinutes(1);

    private static final double DEFAULT_STDDEV_THRESHOLD = 1.0;
    private static final int DEFAULT_MIN_SAMPLES_BEFORE_SUCCESS = 10;
    private static final int DEFAULT_MAX_SAMPLES_BEFORE_FAILURE = 100;

    private int port;
    private MasterServerConfiguration masterServerConfiguration;
    private long masterLookupIntervalMillis;
    private long masterSyncIntervalMillis;
    private double masterSyncDevThreshold;
    private int masterSyncMinSamplesPerRound;
    private int masterSyncMaxSamplesPerRound;
    private long clientSyncIntervalMillis;
    private double clientSyncDevThreshold;
    private int clientSyncMinSamplesPerRound;
    private int clientSyncMaxSamplesPerRound;

    public GrpcConfiguration() {
        this.port = NetworkUtils.freePort();
        this.masterLookupIntervalMillis = DEFAULT_MASTER_LOOKUP_INTERVAL.toMillis();
        this.masterSyncIntervalMillis = DEFAULT_MASTER_SYNC_INTERVAL.toMillis();
        this.clientSyncIntervalMillis = DEFAULT_CLIENT_SYNC_INTERVAL.toMillis();
    }

    public int getPort() {
        return port;
    }

    @BQConfigProperty("Binding port for the gRPC service." +
            "If this option is not specified, then a random port will be used.")
    public void setPort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }
        this.port = port;
    }

    @BQConfigProperty
    public void setMaster(MasterServerConfiguration masterServerConfiguration) {
        this.masterServerConfiguration = masterServerConfiguration;
    }

    @BQConfigProperty("Period, at which a lookup for master server's address will be performed." +
            " Has no effect, if static master server configuration is used.")
    public void setMasterLookupIntervalMillis(long masterLookupIntervalMillis) {
        if (masterLookupIntervalMillis <= 0) {
            throw new IllegalArgumentException("Invalid master lookup interval (millis): " + masterLookupIntervalMillis);
        }
        this.masterLookupIntervalMillis = masterLookupIntervalMillis;
    }

    @BQConfigProperty("Period, at which time synchronization with master server will be performed.")
    public void setMasterSyncIntervalMillis(long masterSyncIntervalMillis) {
        if (masterSyncIntervalMillis <= 0) {
            throw new IllegalArgumentException("Invalid master sync interval (millis): " + masterSyncIntervalMillis);
        }
        this.masterSyncIntervalMillis = masterSyncIntervalMillis;
    }

    @BQConfigProperty("Period, at which time synchronization with clients will be performed.")
    public void setClientSyncIntervalMillis(long clientSyncIntervalMillis) {
        if (clientSyncIntervalMillis <= 0) {
            throw new IllegalArgumentException("Invalid client sync interval (millis): " + clientSyncIntervalMillis);
        }
        this.clientSyncIntervalMillis = clientSyncIntervalMillis;
    }

    @BQConfigProperty("Standard deviation threshold for master server time sync")
    public void setMasterSyncDevThreshold(double masterSyncDevThreshold) {
        if (masterSyncDevThreshold <= 0.0) {
            throw new IllegalArgumentException("Threshold must be positive: " + masterSyncDevThreshold);
        }
        this.masterSyncDevThreshold = masterSyncDevThreshold;
    }

    @BQConfigProperty("Minimum number of samples to make per round for master server time sync")
    public void setMasterSyncMinSamplesPerRound(int masterSyncMinSamplesPerRound) {
        if (masterSyncMinSamplesPerRound <= 0) {
            throw new IllegalArgumentException("Minimum number of samples to make per round must be positive: " +
                    masterSyncMinSamplesPerRound);
        } else if ((masterSyncMaxSamplesPerRound != 0) && (masterSyncMinSamplesPerRound > masterSyncMaxSamplesPerRound)) {
            throw new IllegalArgumentException("Minimum number of samples to make per round must be less than" +
                    " or equal to maximum number of samples: " +
                    masterSyncMinSamplesPerRound + " > " + masterSyncMaxSamplesPerRound);
        }
        this.masterSyncMinSamplesPerRound = masterSyncMinSamplesPerRound;
    }

    @BQConfigProperty("Maximum number of samples to make per round for master server time sync")
    public void setMasterSyncMaxSamplesPerRound(int masterSyncMaxSamplesPerRound) {
        if (masterSyncMaxSamplesPerRound <= 0) {
            throw new IllegalArgumentException("Maximum number of samples to make per round must be positive: " +
                    masterSyncMaxSamplesPerRound);
        } else if ((masterSyncMinSamplesPerRound != 0) && (masterSyncMaxSamplesPerRound < masterSyncMinSamplesPerRound)) {
            throw new IllegalArgumentException("Maximum number of samples to make per round must be greater than" +
                    " or equal to minimum number of samples: " +
                    masterSyncMaxSamplesPerRound + " < " + masterSyncMinSamplesPerRound);
        }
        this.masterSyncMaxSamplesPerRound = masterSyncMaxSamplesPerRound;
    }

    @BQConfigProperty("Standard deviation threshold for client time sync")
    public void setClientSyncDevThreshold(double clientSyncDevThreshold) {
        if (clientSyncDevThreshold <= 0.0) {
            throw new IllegalArgumentException("Threshold must be positive: " + clientSyncDevThreshold);
        }
        this.clientSyncDevThreshold = clientSyncDevThreshold;
    }

    @BQConfigProperty("Minimum number of samples to make per round for client time sync")
    public void setClientSyncMinSamplesPerRound(int clientSyncMinSamplesPerRound) {
        if (clientSyncMinSamplesPerRound <= 0) {
            throw new IllegalArgumentException("Minimum number of samples to make per round must be positive: " +
                    clientSyncMinSamplesPerRound);
        } else if ((clientSyncMaxSamplesPerRound != 0) && (clientSyncMinSamplesPerRound > clientSyncMaxSamplesPerRound)) {
            throw new IllegalArgumentException("Minimum number of samples to make per round must be less than" +
                    " or equal to maximum number of samples: " +
                    clientSyncMinSamplesPerRound + " > " + clientSyncMaxSamplesPerRound);
        }
        this.clientSyncMinSamplesPerRound = clientSyncMinSamplesPerRound;
    }

    @BQConfigProperty("Maximum number of samples to make per round for client time sync")
    public void setClientSyncMaxSamplesPerRound(int clientSyncMaxSamplesPerRound) {
        if (clientSyncMaxSamplesPerRound <= 0) {
            throw new IllegalArgumentException("Maximum number of samples to make per round must be positive: " +
                    clientSyncMaxSamplesPerRound);
        } else if ((clientSyncMinSamplesPerRound != 0) && (clientSyncMaxSamplesPerRound < clientSyncMinSamplesPerRound)) {
            throw new IllegalArgumentException("Maximum number of samples to make per round must be greater than" +
                    " or equal to minimum number of samples: " +
                    clientSyncMaxSamplesPerRound + " < " + clientSyncMinSamplesPerRound);
        }
        this.clientSyncMaxSamplesPerRound = clientSyncMaxSamplesPerRound;
    }

    public Supplier<InetSocketAddress> createMasterServerAddressFactory(Injector injector) {
        if (masterServerConfiguration == null) {
            return () -> null;
        } else {
            return masterServerConfiguration.getMasterServerAddressFactory(injector);
        }
    }

    public Duration getMasterLookupInterval() {
        return Duration.ofMillis(masterLookupIntervalMillis);
    }

    public Duration getMasterSyncInterval() {
        return Duration.ofMillis(masterSyncIntervalMillis);
    }

    public Duration getClientSyncInterval() {
        return Duration.ofMillis(clientSyncIntervalMillis);
    }

    public double getMasterSyncDevThreshold() {
        return (masterSyncDevThreshold == 0.0) ? DEFAULT_STDDEV_THRESHOLD : masterSyncDevThreshold;
    }

    public int getMasterSyncMinSamplesPerRound() {
        return (masterSyncMinSamplesPerRound == 0) ? DEFAULT_MIN_SAMPLES_BEFORE_SUCCESS : masterSyncMinSamplesPerRound;
    }

    public int getMasterSyncMaxSamplesPerRound() {
        return (masterSyncMaxSamplesPerRound == 0) ? DEFAULT_MAX_SAMPLES_BEFORE_FAILURE : masterSyncMaxSamplesPerRound;
    }

    public double getClientSyncDevThreshold() {
        return (clientSyncDevThreshold == 0.0) ? DEFAULT_STDDEV_THRESHOLD : clientSyncDevThreshold;
    }

    public int getClientSyncMinSamplesPerRound() {
        return (clientSyncMinSamplesPerRound == 0) ? DEFAULT_MIN_SAMPLES_BEFORE_SUCCESS : clientSyncMinSamplesPerRound;
    }

    public int getClientSyncMaxSamplesPerRound() {
        return (clientSyncMaxSamplesPerRound == 0) ? DEFAULT_MAX_SAMPLES_BEFORE_FAILURE : clientSyncMaxSamplesPerRound;
    }
}
