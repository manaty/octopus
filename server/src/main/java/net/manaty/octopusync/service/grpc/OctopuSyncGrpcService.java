package net.manaty.octopusync.service.grpc;

import io.vertx.core.Future;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.api.OctopuSyncGrpc;
import net.manaty.octopusync.api.SendClickRequest;
import net.manaty.octopusync.api.SendClickResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class OctopuSyncGrpcService extends OctopuSyncGrpc.OctopuSyncVertxImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OctopuSyncGrpcService.class);

    private final Vertx vertx;

    public OctopuSyncGrpcService(Vertx vertx) {
        this.vertx = Objects.requireNonNull(vertx);
    }

    @Override
    public void sendClick(SendClickRequest request, Future<SendClickResponse> response) {
        LOGGER.info("Received sendClick request: {}", request);
        response.complete(SendClickResponse.getDefaultInstance());
    }
}
