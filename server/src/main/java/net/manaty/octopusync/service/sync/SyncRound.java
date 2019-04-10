package net.manaty.octopusync.service.sync;

import net.manaty.octopusync.api.SyncTimeRequest;
import net.manaty.octopusync.api.SyncTimeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.meanvar.MeanVarianceSampler;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class SyncRound<R> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncRound.class);

    private static final double STDDEV_THRESHOLD = 1.0;
    private static final int MIN_SAMPLES_BEFORE_SUCCESS = 10;
    private static final int MAX_SAMPLES_BEFORE_FAILURE = 100;

    private final SyncResultBuilder<?, R> resultBuilder;
    private final Consumer<R> resultConsumer;
    private final AtomicLong seqnum;

    private final MeanVarianceSampler sampler;

    private volatile Consumer<SyncTimeRequest> requestConsumer;
    private volatile long sent;

    public SyncRound(SyncResultBuilder<?, R> resultBuilder, Consumer<R> resultConsumer) {
        this.resultBuilder = resultBuilder;
        this.resultConsumer = resultConsumer;
        this.seqnum = new AtomicLong(0);
        this.sampler = new MeanVarianceSampler();
    }

    public void handleResponse(SyncTimeResponse response) throws Exception {
        long seqnum = this.seqnum.get();
        if (seqnum != response.getSeqnum()) {
            throw new IllegalStateException("seqnum does not match");
        } else {
            long received = response.getReceivedTimeUtc();
            long delta = received - sent;
            sampler.add(delta);
            double stddev = sampler.getStdDevUnbiased();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("round: %d/%d with %s, mean: %.2f, var: %.2f, stddev: %.2f",
                        resultBuilder.getRound(), seqnum, resultBuilder.getTargetDescription(),
                        sampler.getMean(), sampler.getVarianceUnbiased(), stddev));
            }
            if (seqnum > MIN_SAMPLES_BEFORE_SUCCESS && stddev < STDDEV_THRESHOLD ) {
                resultConsumer.accept(resultBuilder.ok(System.currentTimeMillis(), delta));
            } else if (seqnum == MAX_SAMPLES_BEFORE_FAILURE) {
                String message = String.format("failed to sync with %s in %d round-trips; stddev is greater than %.2f",
                        resultBuilder.getTargetDescription(), seqnum, STDDEV_THRESHOLD);
                LOGGER.error(message);
                resultConsumer.accept(resultBuilder.failure(System.currentTimeMillis(), new IllegalStateException(message)));
            } else {
                doExecute();
            }
        }
    }

    public synchronized void execute(Consumer<SyncTimeRequest> requestConsumer) {
        if (this.requestConsumer != null) {
            throw new IllegalStateException();
        }
        this.requestConsumer = requestConsumer;
        doExecute();
    }

    private void doExecute() {
        long seqnum = this.seqnum.incrementAndGet();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Executing sync round {}/{} with {}",
                    resultBuilder.getRound(), seqnum, resultBuilder.getTargetDescription());
        }

        sent = System.currentTimeMillis();
        requestConsumer.accept(SyncTimeRequest.newBuilder()
                .setSeqnum(seqnum)
                .build());
    }
}
