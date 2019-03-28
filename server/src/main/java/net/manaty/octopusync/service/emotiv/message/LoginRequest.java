package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginRequest implements Request {

    private final long id;
    private final Map<String, Object> params;

    public LoginRequest(long id, String username, String password, String clientId, String clientSecret) {
        this.id = id;

        Map<String, Object> params = new HashMap<>((int)(4 / 0.75d + 1));
        params.put("username", Objects.requireNonNull(username));
        params.put("password", Objects.requireNonNull(password));
        params.put("client_id", Objects.requireNonNull(clientId));
        params.put("client_secret", Objects.requireNonNull(clientSecret));
        this.params = params;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String method() {
        return "login";
    }

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
