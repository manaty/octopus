package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

import java.util.Map;

public class GetUserLoginRequest implements Request {

    private final long id;

    public GetUserLoginRequest(long id) {
        this.id = id;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String method() {
        return "getUserLogin";
    }

    @JsonIgnore
    @Override
    public Map<String, Object> params() {
        return null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .toString();
    }
}
