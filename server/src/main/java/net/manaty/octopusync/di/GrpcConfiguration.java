package net.manaty.octopusync.di;

import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.NetworkUtils;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collections;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@BQConfig
public class GrpcConfiguration {

    private static final Duration DEFAULT_MASTER_LOOKUP_INTERVAL = Duration.ofSeconds(5);
    private static final Duration DEFAULT_MASTER_SYNC_INTERVAL = Duration.ofMinutes(1);

    private int port;
    private MasterServerConfiguration masterServerConfiguration;
    private long masterLookupIntervalMillis;
    private long masterSyncIntervalMillis;

    public GrpcConfiguration() {
        this.port = NetworkUtils.freePort();
        this.masterLookupIntervalMillis = DEFAULT_MASTER_LOOKUP_INTERVAL.toMillis();
        this.masterSyncIntervalMillis = DEFAULT_MASTER_SYNC_INTERVAL.toMillis();
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

    @BQConfigProperty
    public void setMasterLookupIntervalMillis(long masterLookupIntervalMillis) {
        if (masterLookupIntervalMillis <= 0) {
            throw new IllegalArgumentException("Invalid master lookup interval (millis): " + masterLookupIntervalMillis);
        }
        this.masterLookupIntervalMillis = masterLookupIntervalMillis;
    }

    @BQConfigProperty
    public void setMasterSyncIntervalMillis(long masterSyncIntervalMillis) {
        if (masterSyncIntervalMillis <= 0) {
            throw new IllegalArgumentException("Invalid master sync interval (millis): " + masterSyncIntervalMillis);
        }
        this.masterSyncIntervalMillis = masterSyncIntervalMillis;
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
}
