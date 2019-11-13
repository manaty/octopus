package net.manaty.octopusync.service.sync;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class SyncResultBuilder<B extends SyncResultBuilder<B, R>, R> {

    private final long round;
    private final List<SyncMeasurement> measurements;

    protected SyncResultBuilder(long round, int maxSamples) {
        this.round = round;
        this.measurements = new ArrayList<>(maxSamples + 1);
    }

    public abstract B newBuilderForRound(long round);

    public final long getRound() {
        return round;
    }

    public final R ok(long finished, long delay) {
        return buildResult(round, measurements, finished, delay, null);
    }

    public final R failure(long finished, String errorMessage) {
        Objects.requireNonNull(errorMessage);
        return buildResult(round, measurements, finished, 0, errorMessage);
    }

    public final R failure(long finished, Throwable error) {
        Objects.requireNonNull(error);
        return buildResult(round, measurements, finished, 0, buildErrorMessage(error));
    }

    protected abstract R buildResult(long round, List<SyncMeasurement> measurements,
                                     long finished, long delay, @Nullable String errorMessage);

    private static String buildErrorMessage(Throwable error) {
        StringBuilder buf = new StringBuilder();
        do {
            if (buf.length() > 0) {
                buf.append(" => ");
            }
            buf.append(error.getClass().getName());
            if (error.getMessage() != null) {
                buf.append(": ");
                buf.append(error.getMessage());
            }
            error = error.getCause();
        } while (error != null);
        return buf.toString();
    }

    public abstract String getTargetDescription();

    public void addMeasurement(long seqnum, long sent, long received, long rtt, long delta,
                               double mean, double varianceUnbiased, double stddev) {
        measurements.add(new SyncMeasurement(seqnum, sent, received, rtt, delta, mean, varianceUnbiased, stddev));
    }
}
