package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import net.manaty.octopusync.service.emotiv.ISO8601OffsetDateTimeDeserializer;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Session {

    public enum Status {
        OPENED, ACTIVE, ACTIVATED, CLOSED;

        public static Status forName(String name) {
            Objects.requireNonNull(name);
            for (Status status : values()) {
                if (status.name().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status name: " + name);
        }

        public String protocolValue() {
            return name().toLowerCase();
        }

        @Override
        public String toString() {
            return protocolValue();
        }
    }

    public static boolean hasStatus(Session session, Status status) {
        Objects.requireNonNull(status);
        return Session.Status.forName(session.getStatus()).equals(status);
    }

    public static Status getStatus(Session session) {
        return Session.Status.forName(session.status);
    }

    public static Comparator<Session> comparator = new Comparator<Session>() {
        private final Map<Status, Integer> STATUS_ORDER;

        {
            Map<Status, Integer> STATUS_ORDER = new HashMap<>();
            STATUS_ORDER.put(Status.CLOSED, 1);
            STATUS_ORDER.put(Status.OPENED, 2);
            STATUS_ORDER.put(Status.ACTIVATED, 3);
            STATUS_ORDER.put(Status.ACTIVE, 3);
            this.STATUS_ORDER = STATUS_ORDER;
        }

        @Override
        public int compare(Session s1, Session s2) {
            int statusOrder = Integer.compare(
                    STATUS_ORDER.get(Session.getStatus(s1)), STATUS_ORDER.get(Session.getStatus(s2)));
            if (statusOrder == 0) {
                return s1.getStarted().compareTo(s2.getStarted());
            } else {
                return statusOrder;
            }
        }
    };

    private String appId;
    private String id;
    private String license;
    private String owner;
    private String status;

    @JsonDeserialize(using = ISO8601OffsetDateTimeDeserializer.class)
    private LocalDateTime started;

    @JsonDeserialize(using = ISO8601OffsetDateTimeDeserializer.class)
    private LocalDateTime stopped;

    private Headset headset;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStarted() {
        return started;
    }

    public void setStarted(LocalDateTime started) {
        this.started = started;
    }

    public LocalDateTime getStopped() {
        return stopped;
    }

    public void setStopped(LocalDateTime stopped) {
        this.stopped = stopped;
    }

    public Headset getHeadset() {
        return headset;
    }

    public void setHeadset(Headset headset) {
        this.headset = headset;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("appId", appId)
                .add("id", id)
                .add("license", license)
                .add("owner", owner)
                .add("status", status)
                .add("started", started)
                .add("stopped", stopped)
                .add("headset", headset)
                .toString();
    }
}
