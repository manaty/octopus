package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;

public class ClientTimeSyncResult {

    private final String headsetId;
    private final long round;
    private final long finished;
    private final long delay;
    private final String error;

    public ClientTimeSyncResult(String headsetId, long round, long finished, long delay, String error) {
        this.headsetId = headsetId;
        this.round = round;
        this.finished = finished;
        this.delay = delay;
        this.error = error;
    }

    public String getHeadsetId() {
        return headsetId;
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

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("headsetId", headsetId)
                .add("round", round)
                .add("finished", finished)
                .add("delay", delay)
                .add("error", error)
                .toString();
    }
}
