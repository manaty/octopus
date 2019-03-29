package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SubscribeRequest implements Request {

    private final long id;
    private final Map<String, Object> params;

    public SubscribeRequest(long id, String authzToken, Set<String> streams, String sessionId) {
        this.id = id;

        Map<String, Object> params = new HashMap<>((int)(4 / 0.75d + 1));
        params.put("_auth", Objects.requireNonNull(authzToken));
        params.put("streams", Objects.requireNonNull(streams));
        params.put("session", Objects.requireNonNull(sessionId));
        params.put("replay", false);
        this.params = params;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String method() {
        return "subscribe";
    }

    @Nullable
    @Override
    public Map<String, Object> params() {
        return params;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("params", params)
                .toString();
    }
}
