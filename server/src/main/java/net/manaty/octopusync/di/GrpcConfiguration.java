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

    private int port;
    private MasterServerConfiguration masterServerConfiguration;
    private long masterLookupIntervalMillis;
    private long masterSyncIntervalMillis;
    private long clientSyncIntervalMillis;

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
}
