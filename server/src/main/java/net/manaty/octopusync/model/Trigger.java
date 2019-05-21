package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;

public class Trigger implements Timestamped {

    private final long id;
    private final long happenedTimeMillisUtc;
    private final String message;

    public Trigger(long id, long happenedTimeMillisUtc, String message) {
        this.id = id;
        this.happenedTimeMillisUtc = happenedTimeMillisUtc;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public long getHappenedTimeMillisUtc() {
        return happenedTimeMillisUtc;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("happenedTimeMillisUtc", happenedTimeMillisUtc)
                .add("message", message)
                .toString();
    }

    @Override
    public long timestamp() {
        return happenedTimeMillisUtc;
    }
}
