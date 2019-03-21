package net.manaty.octopusync.service.s2s;

import io.grpc.Status;
import io.grpc.StatusException;
import io.vertx.grpc.GrpcBidiExchange;
import io.vertx.reactivex.core.Future;
import net.manaty.octopusync.s2s.api.SyncTimeRequest;
import net.manaty.octopusync.s2s.api.SyncTimeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

public class SyncRound {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncRound.class);

    private final InetSocketAddress nodeAddress;
    private final long round;
    private final GrpcBidiExchange<SyncTimeResponse, SyncTimeRequest> exchange;
    private final Future<SyncResult> future;
    private final AtomicLong seqnum;

    private volatile long sent;

    public SyncRound(
            InetSocketAddress nodeAddress,
            long round,
            GrpcBidiExchange<SyncTimeResponse, SyncTimeRequest> exchange,
            Future<SyncResult> future) {

        this.nodeAddress = nodeAddress;
        this.round = round;
        this.exchange = exchange;
        this.future = future;
        this.seqnum = new AtomicLong(0);

        exchange.handler(response -> {
            if (seqnum.get() != response.getSeqnum()) {
                future.complete(SyncResult.failure(nodeAddress, round, System.currentTimeMillis(),
                        new IllegalStateException("seqnum does not match")));
                exchange.fail(new StatusException(Status.INVALID_ARGUMENT.withDescription("seqnum does not match")));
            } else {
                long received = response.getReceivedTimeUtc();
                long delta = received - sent;
                // TODO: temporary for quick testing
                if (seqnum.get() == 10) {
                    future.complete(SyncResult.ok(nodeAddress, round, System.currentTimeMillis(), delta));
                    exchange.end();
                } else {
                    execute();
                }
            }
        });

        exchange.exceptionHandler(e -> {
            if (!future.isComplete()) {
                future.complete(SyncResult.failure(nodeAddress, round, System.currentTimeMillis(), e));
            }
        });
    }

    public void execute() {
        long seqnum = this.seqnum.incrementAndGet();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing sync round {}/{} with {}", round, seqnum, nodeAddress);
        }

        sent = System.currentTimeMillis();
        try {
            exchange.write(SyncTimeRequest.newBuilder()
                    .setSeqnum(seqnum)
                    .build());
        } catch (Exception e) {
            future.complete(SyncResult.failure(nodeAddress, round, System.currentTimeMillis(),
                    new Exception("Failed to write to bidi exchange", e)));
        }
    }
}
