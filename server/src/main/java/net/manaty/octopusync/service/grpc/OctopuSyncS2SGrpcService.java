package net.manaty.octopusync.service.grpc;

import io.vertx.grpc.GrpcBidiExchange;
import net.manaty.octopusync.s2s.api.OctopuSyncS2SGrpc;
import net.manaty.octopusync.api.SyncTimeRequest;
import net.manaty.octopusync.api.SyncTimeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OctopuSyncS2SGrpcService extends OctopuSyncS2SGrpc.OctopuSyncS2SVertxImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(OctopuSyncS2SGrpcService.class);

    @Override
    public void syncTime(GrpcBidiExchange<SyncTimeRequest, SyncTimeResponse> exchange) {
        exchange.exceptionHandler(e -> LOGGER.error("S2S bidi exchange failed", e));
        exchange.handler(request -> {
            try {
                exchange.write(SyncTimeResponse.newBuilder()
                        .setSeqnum(request.getSeqnum())
                        .setReceivedTimeUtc(System.currentTimeMillis())
                        .build());
            } catch (Exception e) {
                LOGGER.error("Failed to write to bidi exchange", e);
            }
        });
    }
}