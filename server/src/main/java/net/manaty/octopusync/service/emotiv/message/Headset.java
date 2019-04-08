package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

public class Headset {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .toString();
    }
}
