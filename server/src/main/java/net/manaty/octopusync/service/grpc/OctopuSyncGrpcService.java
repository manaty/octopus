package net.manaty.octopusync.service.grpc;

import io.vertx.core.Future;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.api.*;
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
    public void createSession(CreateSessionRequest request, Future<CreateSessionResponse> response) {
        super.createSession(request, response);
    }

    @Override
    public void updateState(UpdateStateRequest request, Future<UpdateStateResponse> response) {
        LOGGER.info("Received updateState request: {}", request);
        response.complete(UpdateStateResponse.getDefaultInstance());
    }
}
