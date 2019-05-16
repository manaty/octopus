package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;

public class MoodState implements Timestamped {

    private final String headsetId;
    private final String state;
    private final long sinceTimeUtc;

    public MoodState(String headsetId, String state, long sinceTimeUtc) {
        this.headsetId = headsetId;
        this.state = state;
        this.sinceTimeUtc = sinceTimeUtc;
    }

    public String getHeadsetId() {
        return headsetId;
    }

    public String getState() {
        return state;
    }

    public long getSinceTimeUtc() {
        return sinceTimeUtc;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("headsetId", headsetId)
                .add("state", state)
                .add("sinceTimeUtc", sinceTimeUtc)
                .toString();
    }

    @Override
    public long timestamp() {
        return sinceTimeUtc;
    }
}
