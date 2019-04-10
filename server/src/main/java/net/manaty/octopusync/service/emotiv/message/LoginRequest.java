package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonTypeName(JSONRPC.METHOD_LOGIN)
public class LoginRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public LoginRequest() {
        // for Jackson
    }

    public LoginRequest(long id, String username, String password, String clientId, String clientSecret) {
        super(id, buildParams(username, password, clientId, clientSecret));
    }

    private static Map<String, Object> buildParams(String username, String password, String clientId, String clientSecret) {
        Map<String, Object> params = new HashMap<>((int)(4 / 0.75d + 1));
        params.put("username", Objects.requireNonNull(username));
        params.put("password", Objects.requireNonNull(password));
        params.put("client_id", Objects.requireNonNull(clientId));
        params.put("client_secret", Objects.requireNonNull(clientSecret));
        return params;
    }
}
