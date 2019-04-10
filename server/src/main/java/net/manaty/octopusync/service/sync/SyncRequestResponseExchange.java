package net.manaty.octopusync.service.sync;

import io.grpc.Status;
import io.vertx.grpc.GrpcBidiExchange;
import net.manaty.octopusync.api.SyncTimeRequest;
import net.manaty.octopusync.api.SyncTimeResponse;

public interface SyncRequestResponseExchange {

    static SyncRequestResponseExchange wrap(GrpcBidiExchange<SyncTimeResponse, SyncTimeRequest> exchange) {
        return new SyncRequestResponseExchange() {
            @Override
            public void write(SyncTimeRequest request) {
                exchange.write(request);
            }

            @Override
            public void end() {
                exchange.end();
            }

            @Override
            public void fail(Throwable e) {
                exchange.fail(Status.fromThrowable(e).asRuntimeException());
            }
        };
    }

    void write(SyncTimeRequest request) throws Exception;

    void end();

    void fail(Throwable e);
}
