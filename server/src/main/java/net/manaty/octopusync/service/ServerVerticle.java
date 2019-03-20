package net.manaty.octopusync.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.vertx.reactivex.core.AbstractVerticle;
import net.manaty.octopusync.service.grpc.OctopuSyncGrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerVerticle.class);

    private final int grpcPort;

    private volatile Server grpcServer;

    public ServerVerticle(int grpcPort) {
        this.grpcPort = grpcPort;
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Initiating verticle startup");

        LOGGER.info("Launching gRPC server on port {}", grpcPort);
        grpcServer = ServerBuilder.forPort(grpcPort)
                .addService(new OctopuSyncGrpcService(vertx))
                .build();
        grpcServer.start();
        LOGGER.info("Launched gRPC server on port {}", grpcServer.getPort());
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Initiating verticle shutdown");

        Server grpcServer = this.grpcServer;
        if (grpcServer != null) {
            LOGGER.info("Shutting down gRPC server on port {}", grpcPort);
            grpcServer.shutdownNow().awaitTermination();
        }
    }
}
