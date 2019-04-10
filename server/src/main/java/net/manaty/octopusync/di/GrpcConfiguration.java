package net.manaty.octopusync.di;

import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.NetworkUtils;
import net.manaty.octopusync.service.s2s.NodeListFactory;

import java.time.Duration;
import java.util.Collections;

@SuppressWarnings("unused")
@BQConfig
public class GrpcConfiguration {

    private static final Duration DEFAULT_NODE_LOOKUP_INTERVAL = Duration.ofSeconds(5);
    // TODO: sync once in 1 minute might be insufficient; need to investigate about typical clock drift
    private static final Duration DEFAULT_NODE_SYNC_INTERVAL = Duration.ofMinutes(1);

    private int port;
    private NodeListFactoryConfiguration nodeListFactoryConfiguration;
    private long nodeLookupIntervalMillis;
    private long nodeSyncIntervalMillis;

    public GrpcConfiguration() {
        this.port = NetworkUtils.freePort();
        this.nodeLookupIntervalMillis = DEFAULT_NODE_LOOKUP_INTERVAL.toMillis();
        this.nodeSyncIntervalMillis = DEFAULT_NODE_SYNC_INTERVAL.toMillis();
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
    public void setNodes(NodeListFactoryConfiguration nodeListFactoryConfiguration) {
        this.nodeListFactoryConfiguration = nodeListFactoryConfiguration;
    }

    @BQConfigProperty
    public void setNodeLookupIntervalMillis(long nodeLookupIntervalMillis) {
        if (nodeLookupIntervalMillis <= 0) {
            throw new IllegalArgumentException("Invalid node lookup interval (millis): " + nodeLookupIntervalMillis);
        }
        this.nodeLookupIntervalMillis = nodeLookupIntervalMillis;
    }

    @BQConfigProperty
    public void setNodeSyncIntervalMillis(long nodeSyncIntervalMillis) {
        if (nodeSyncIntervalMillis <= 0) {
            throw new IllegalArgumentException("Invalid node sync interval (millis): " + nodeSyncIntervalMillis);
        }
        this.nodeSyncIntervalMillis = nodeSyncIntervalMillis;
    }

    public NodeListFactory createNodeListFactory(Injector injector) {
        if (nodeListFactoryConfiguration == null) {
            return Collections::emptyList;
        } else {
            return nodeListFactoryConfiguration.getNodeListFactory(injector);
        }
    }

    public Duration getNodeLookupInterval() {
        return Duration.ofMillis(nodeLookupIntervalMillis);
    }

    public Duration getNodeSyncInterval() {
        return Duration.ofMillis(nodeSyncIntervalMillis);
    }
}
