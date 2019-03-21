package net.manaty.octopusync.di;

import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.s2s.NodeListFactory;

import java.util.Collections;

@SuppressWarnings("unused")
@BQConfig
public class GrpcConfiguration {

    private int port;
    private NodeListFactoryConfiguration nodeListFactoryConfiguration;

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

    public NodeListFactory createNodeListFactory(Injector injector) {
        if (nodeListFactoryConfiguration == null) {
            return Collections::emptyList;
        } else {
            return nodeListFactoryConfiguration.getNodeListFactory(injector);
        }
    }
}
