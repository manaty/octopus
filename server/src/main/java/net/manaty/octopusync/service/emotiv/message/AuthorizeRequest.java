package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonTypeName(JSONRPC.METHOD_AUTHORIZE)
public class AuthorizeRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public AuthorizeRequest() {
        // for Jackson
    }

    public AuthorizeRequest(long id, String clientId, String clientSecret, @Nullable String license, int debit) {
        super(id, buildParams(clientId, clientSecret, license, debit));
    }

    private static Map<String, Object> buildParams(String clientId, String clientSecret, String license, int debit) {
        Map<String, Object> params = new HashMap<>((int)(4 / 0.75d + 1));
        params.put("client_id", Objects.requireNonNull(clientId));
        params.put("client_secret", Objects.requireNonNull(clientSecret));
        if (license != null) {
            params.put("license", license);
        }
        params.put("debit", debit);
        return params;
    }
}
