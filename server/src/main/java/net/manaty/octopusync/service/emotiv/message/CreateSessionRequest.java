package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonTypeName(JSONRPC.METHOD_CREATESESSION)
public class CreateSessionRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public CreateSessionRequest() {
        // for Jackson
    }

    public CreateSessionRequest(long id, String authzToken, String headset, String status) {
        super(id, buildParams(authzToken, headset, status));
    }

    private static Map<String, Object> buildParams(String authzToken, String headset, String status) {
        Map<String, Object> params = new HashMap<>((int)(3 / 0.75d + 1));
        params.put("_auth", Objects.requireNonNull(authzToken));
        params.put("headset", Objects.requireNonNull(headset));
        params.put("status", Objects.requireNonNull(status));
        return params;
    }
}
