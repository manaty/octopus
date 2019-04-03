package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.manaty.octopusync.service.emotiv.ISO8601OffsetDateTimeDeserializer;

import java.time.LocalDateTime;

public class Session {

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
}
