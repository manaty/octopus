package net.manaty.octopusync.service.sync;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class SyncResultBuilder<B extends SyncResultBuilder<B, R>, R> {

    private final long round;

    protected SyncResultBuilder(long round) {
        this.round = round;
    }

    public abstract B newBuilderForRound(long round);

    public final long getRound() {
        return round;
    }

    public final R ok(long finished, long delay) {
        return buildResult(round, finished, delay, null);
    }

    public final R failure(long finished, String errorMessage) {
        Objects.requireNonNull(errorMessage);
        return buildResult(round, finished, 0, errorMessage);
    }

    public final R failure(long finished, Throwable error) {
        Objects.requireNonNull(error);
        return buildResult(round, finished, 0, buildErrorMessage(error));
    }

    protected abstract R buildResult(long round, long finished, long delay, @Nullable String errorMessage);

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
}
