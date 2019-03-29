package net.manaty.octopusync.service.emotiv.message;

import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AuthorizeRequest implements Request {

    private final long id;
    private final Map<String, Object> params;

    public AuthorizeRequest(long id, String clientId, String clientSecret, @Nullable String license, int debit) {
        this.id = id;

        Map<String, Object> params = new HashMap<>((int)(4 / 0.75d + 1));
        params.put("client_id", Objects.requireNonNull(clientId));
        params.put("client_secret", Objects.requireNonNull(clientSecret));
        if (license != null) {
            params.put("license", license);
        }
        params.put("debit", debit);

        this.params = params;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String method() {
        return "authorize";
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
