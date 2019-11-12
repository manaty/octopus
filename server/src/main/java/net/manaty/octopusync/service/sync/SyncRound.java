package net.manaty.octopusync.service.sync;

import net.manaty.octopusync.api.SyncTimeRequest;
import net.manaty.octopusync.api.SyncTimeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.meanvar.MeanVarianceSlidingWindow;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class SyncRound<R> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncRound.class);

    private final SyncResultBuilder<?, R> resultBuilder;
    private final Consumer<R> resultConsumer;
    private final double devThreshold;
    private final int minSamples;
    private final int maxSamples;
    private final AtomicLong seqnum;

    private final MeanVarianceSlidingWindow sampler;

    private volatile Consumer<SyncTimeRequest> requestConsumer;
    private volatile long sent;

    /**
     * @param devThreshold Standard deviation threshold;
     *                     if the result is smaller than this value, it's considered a success
     * @param minSamples Minimum number of samples to make
     * @param maxSamples Maximum number of samples to make
     */
    public SyncRound(
            SyncResultBuilder<?, R> resultBuilder,
            Consumer<R> resultConsumer,
            double devThreshold,
            int minSamples,
            int maxSamples) {

        this.resultBuilder = resultBuilder;
        this.resultConsumer = resultConsumer;
        this.devThreshold = devThreshold;
        this.minSamples = minSamples;
        this.maxSamples = maxSamples;
        this.seqnum = new AtomicLong(0);
        this.sampler = new MeanVarianceSlidingWindow(minSamples);
    }

    public void handleResponse(SyncTimeResponse response) {
        long seqnum = this.seqnum.get();
        if (seqnum != response.getSeqnum()) {
            throw new IllegalStateException("seqnum does not match value in other party's response" +
                    " (" + seqnum + " <> " + response.getSeqnum() + ")");
        } else {
            double rtt = System.currentTimeMillis() - sent;
            long received = response.getReceivedTimeUtc();
            double delta = received - sent - rtt / 2;
            sampler.update(delta);
            double stddev = sampler.getStdDev();
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format("round: %d/%d with %s, mean: %.2f, var: %.2f, stddev: %.2f",
                        resultBuilder.getRound(), seqnum, resultBuilder.getTargetDescription(),
                        sampler.getMean(), sampler.getVarianceUnbiased(), stddev));
            }
            resultBuilder.addMeasurement(seqnum, sent, received, Math.round(delta),
                    sampler.getMean(), sampler.getVarianceUnbiased(), stddev);
            if (seqnum > minSamples && stddev < devThreshold) {
                resultConsumer.accept(resultBuilder.ok(System.currentTimeMillis(), Math.round(sampler.getMean())));
            } else if (seqnum == maxSamples) {
                String message = String.format("failed to sync with %s in %d round-trips; stddev is greater than %.2f",
                        resultBuilder.getTargetDescription(), seqnum, devThreshold);
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

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Executing sync round {}/{} with {}",
                    resultBuilder.getRound(), seqnum, resultBuilder.getTargetDescription());
        }

        sent = System.currentTimeMillis();
        requestConsumer.accept(SyncTimeRequest.newBuilder()
                .setSeqnum(seqnum)
                .build());
    }
}
