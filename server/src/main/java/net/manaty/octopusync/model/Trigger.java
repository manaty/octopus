package net.manaty.octopusync.model;

import com.google.common.base.MoreObjects;

public class Trigger implements Timestamped {

    private static final char RESERVED_MESSAGE_INDICATOR = '~';
    public static final String MESSAGE_MUSICON = "~musicon";
    public static final String MESSAGE_MUSICOFF = "~musicoff";

    public static Trigger message(long id, long happenedTimeMillisUtc, String message) {
        if (message.charAt(0) == RESERVED_MESSAGE_INDICATOR) {
            throw new IllegalArgumentException("Invalid message: " + message);
        }
        return new Trigger(id, happenedTimeMillisUtc, message);
    }

    public static Trigger musicOn(long id, long happenedTimeMillisUtc) {
        return new Trigger(id, happenedTimeMillisUtc, MESSAGE_MUSICON);
    }

    public static Trigger musicOff(long id, long happenedTimeMillisUtc) {
        return new Trigger(id, happenedTimeMillisUtc, MESSAGE_MUSICOFF);
    }

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
