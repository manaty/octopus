package net.manaty.octopusync.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.vertx.reactivex.core.AbstractVerticle;
import net.manaty.octopusync.service.grpc.OctopuSyncGrpcService;
import net.manaty.octopusync.service.grpc.OctopuSyncS2SGrpcService;
import net.manaty.octopusync.service.s2s.S2STimeSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerVerticle.class);

    private final int grpcPort;
    private final S2STimeSynchronizer synchronizer;

    private volatile Server grpcServer;

    public ServerVerticle(int grpcPort, S2STimeSynchronizer synchronizer) {
        this.grpcPort = grpcPort;
        this.synchronizer = synchronizer;
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Initiating verticle startup");

        LOGGER.info("Launching gRPC server on port {}", grpcPort);
        grpcServer = ServerBuilder.forPort(grpcPort)
                .addService(new OctopuSyncGrpcService(vertx))
                .addService(new OctopuSyncS2SGrpcService())
                .build();
        grpcServer.start();
        LOGGER.info("Launched gRPC server on port {}", grpcServer.getPort());

        LOGGER.info("Starting S2S time synchronizer");
        synchronizer.startSync()
                .subscribe(syncResult -> {
                    // TODO: this is for quick manual testing
                    LOGGER.info("Sync result: {}", syncResult);
                });
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Initiating verticle shutdown");

        // shutdown in reverse order

        LOGGER.info("Stopping S2S time synchronizer");
        synchronizer.stopSync();

        Server grpcServer = this.grpcServer;
        if (grpcServer != null) {
            LOGGER.info("Shutting down gRPC server on port {}", grpcPort);
            grpcServer.shutdownNow().awaitTermination();
        }
    }
}
