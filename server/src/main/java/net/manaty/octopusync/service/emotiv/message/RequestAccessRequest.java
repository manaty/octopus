package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonTypeName(JSONRPC.METHOD_REQUESTACCESS)
public class RequestAccessRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public RequestAccessRequest() {
        // for Jackson
    }

    public RequestAccessRequest(long id, String clientId, String clientSecret) {
        super(id, buildParams(clientId, clientSecret));
    }

    private static Map<String, Object> buildParams(String clientId, String clientSecret) {
        Map<String, Object> params = new HashMap<>((int)(2 / 0.75d + 1));
        params.put("clientId", Objects.requireNonNull(clientId));
        params.put("clientSecret", Objects.requireNonNull(clientSecret));
        return params;
    }
}
