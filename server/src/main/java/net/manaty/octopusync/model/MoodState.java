package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;

public class MoodState implements Timestamped {

    private String headsetId;
    private String state;
    private long sinceTimeUtc;

    public MoodState(String headsetId, String state, long sinceTimeUtc) {
        this.headsetId = headsetId;
        this.state = state;
        this.sinceTimeUtc = sinceTimeUtc;
    }

    public String getHeadsetId() {
        return headsetId;
    }

    public void setHeadsetId(String headsetId) {
        this.headsetId = headsetId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getSinceTimeUtc() {
        return sinceTimeUtc;
    }

    public void setSinceTimeUtc(long sinceTimeUtc) {
        this.sinceTimeUtc = sinceTimeUtc;
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
