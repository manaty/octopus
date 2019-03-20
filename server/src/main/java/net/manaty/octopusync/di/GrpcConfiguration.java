package net.manaty.octopusync.di;

import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.config.PolymorphicConfiguration;

@SuppressWarnings("unused")
@BQConfig
public class GrpcConfiguration implements PolymorphicConfiguration {

    private int port;

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
}
