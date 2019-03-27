package net.manaty.octopusync.service.s2s;

import io.grpc.Status;
import io.grpc.StatusException;
import io.vertx.grpc.GrpcBidiExchange;
import io.vertx.reactivex.core.Future;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.api.SyncTimeResponse;
import net.manaty.octopusync.api.SyncTimeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.meanvar.MeanVarianceSampler;

import java.util.concurrent.atomic.AtomicLong;

public class SyncRound {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncRound.class);

    private static final double STDDEV_THRESHOLD = 1.0;
    private static final int MIN_SAMPLES_BEFORE_SUCCESS = 10;
    private static final int MAX_SAMPLES_BEFORE_FAILURE = 100;

    private final SyncResultBuilder resultBuilder;
    private final GrpcBidiExchange<SyncTimeResponse, SyncTimeRequest> exchange;
    private final Future<S2STimeSyncResult> future;
    private final AtomicLong seqnum;

    private final MeanVarianceSampler sampler;

    private volatile long sent;

    public SyncRound(
            SyncResultBuilder resultBuilder,
            GrpcBidiExchange<SyncTimeResponse, SyncTimeRequest> exchange,
            Future<S2STimeSyncResult> future) {

        this.resultBuilder = resultBuilder;
        this.exchange = exchange;
        this.future = future;
        this.seqnum = new AtomicLong(0);
        this.sampler = new MeanVarianceSampler();

        exchange.handler(response -> {
            long seqnum = this.seqnum.get();
            if (seqnum != response.getSeqnum()) {
                future.complete(resultBuilder.failure(System.currentTimeMillis(),
                        new IllegalStateException("seqnum does not match")));
                exchange.fail(new StatusException(Status.INVALID_ARGUMENT.withDescription("seqnum does not match")));
            } else {
                long received = response.getReceivedTimeUtc();
                long delta = received - sent;
                sampler.add(delta);
                double stddev = sampler.getStdDevUnbiased();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("round: %d/%d with %s, mean: %.2f, var: %.2f, stddev: %.2f",
                            resultBuilder.getRound(), seqnum, resultBuilder.getRemoteAddress(),
                            sampler.getMean(), sampler.getVarianceUnbiased(), stddev));
                }
                if (seqnum > MIN_SAMPLES_BEFORE_SUCCESS && stddev < STDDEV_THRESHOLD ) {
                    future.complete(resultBuilder.ok(System.currentTimeMillis(), delta));
                    exchange.end();
                } else if (seqnum == MAX_SAMPLES_BEFORE_FAILURE) {
                    String message = String.format("failed to sync with %s in %d round-trips; stddev is greater than %.2f",
                            resultBuilder.getRemoteAddress(), seqnum, STDDEV_THRESHOLD);
                    LOGGER.error(message);
                    future.complete(resultBuilder.failure(System.currentTimeMillis(), new IllegalStateException(message)));
                    exchange.end();
                } else {
                    execute();
                }
            }
        });

        exchange.exceptionHandler(e -> {
            if (!future.isComplete()) {
                future.complete(resultBuilder.failure(System.currentTimeMillis(), e));
            }
        });
    }

    public void execute() {
        long seqnum = this.seqnum.incrementAndGet();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing sync round {}/{} with {}",
                    resultBuilder.getRound(), seqnum, resultBuilder.getRemoteAddress());
        }

        sent = System.currentTimeMillis();
        try {
            exchange.write(SyncTimeRequest.newBuilder()
                    .setSeqnum(seqnum)
                    .build());
        } catch (Exception e) {
            future.complete(resultBuilder.failure(System.currentTimeMillis(),
                    new Exception("Failed to write to bidi exchange", e)));
        }
    }
}
