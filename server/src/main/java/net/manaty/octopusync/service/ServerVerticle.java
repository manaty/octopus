package net.manaty.octopusync.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.reactivex.Completable;
import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.RxHelper;
import net.manaty.octopusync.service.db.Storage;
import net.manaty.octopusync.service.emotiv.CortexService;
import net.manaty.octopusync.service.grpc.OctopuSyncGrpcService;
import net.manaty.octopusync.service.grpc.OctopuSyncS2SGrpcService;
import net.manaty.octopusync.service.s2s.S2STimeSynchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerVerticle.class);

    private final int grpcPort;
    private final Storage storage;
    private final S2STimeSynchronizer synchronizer;
    private final CortexService cortexService;

    private volatile Server grpcServer;

    public ServerVerticle(
            int grpcPort,
            Storage storage,
            S2STimeSynchronizer synchronizer,
            CortexService cortexService) {

        this.grpcPort = grpcPort;
        this.storage = storage;
        this.synchronizer = synchronizer;
        this.cortexService = cortexService;
    }

    @Override
    public void start(Future<Void> startFuture) {
        LOGGER.info("Initiating verticle startup");
        Completable.fromAction(() -> {
            LOGGER.info("Launching gRPC server on port {}", grpcPort);
            grpcServer = ServerBuilder.forPort(grpcPort)
                    .addService(new OctopuSyncGrpcService(vertx))
                    .addService(new OctopuSyncS2SGrpcService())
                    .build();
            try {
                grpcServer.start();
            } catch (IOException e) {
                throw new IllegalStateException("Failed to launch gRPC server", e);
            }
        }).andThen(Completable.defer(() -> {
            LOGGER.info("Starting Cortex capture");
            return cortexService.startCapture();
        })).doOnComplete(() -> {
            LOGGER.info("Starting S2S time synchronizer");
            synchronizer.startSync()
                    .flatMapCompletable(storage::save)
                    .subscribe();
        }).subscribeOn(RxHelper.blockingScheduler(vertx))
                .subscribe(startFuture::complete, startFuture::fail);
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("Initiating verticle shutdown");

        // shutdown in reverse order

        LOGGER.info("Stopping S2S time synchronizer");
        synchronizer.stopSync();

        cortexService.stopCapture()
                .onErrorComplete()
                .subscribe();

        Server grpcServer = this.grpcServer;
        if (grpcServer != null) {
            LOGGER.info("Shutting down gRPC server on port {}", grpcPort);
            grpcServer.shutdownNow().awaitTermination();
        }
    }
}
