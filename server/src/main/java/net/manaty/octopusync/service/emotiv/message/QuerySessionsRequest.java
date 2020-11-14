package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonTypeName(JSONRPC.METHOD_QUERYSESSIONS)
public class QuerySessionsRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public QuerySessionsRequest() {
        // for Jackson
    }

    public QuerySessionsRequest(long id, String authzToken, String appId) {
        super(id, buildParams(authzToken, appId));
    }

    private static Map<String, Object> buildParams(String authzToken, String appId) {
        Map<String, Object> params = new HashMap<>((int)(2 / 0.75d + 1));
        params.put("cortexToken", Objects.requireNonNull(authzToken));
        return params;
    }
}
