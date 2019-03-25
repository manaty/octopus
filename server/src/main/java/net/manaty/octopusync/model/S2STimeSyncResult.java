package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;

public class S2STimeSyncResult {

    private final String localAddress;
    private final String remoteAddress;
    private final long round;
    private final long finished;
    private final long delay;
    private final Throwable error;

    public S2STimeSyncResult(
            String localAddress, String remoteAddress,
            long round, long finished, long delay, Throwable error) {

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
        this.round = round;
        this.finished = finished;
        this.delay = delay;
        this.error = error;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public long getRound() {
        return round;
    }

    public long getFinished() {
        return finished;
    }

    public long getDelay() {
        return delay;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("localAddress", localAddress)
                .add("remoteAddress", remoteAddress)
                .add("round", round)
                .add("finished", finished)
                .add("delay", delay)
                .add("error", error)
                .toString();
    }
}
