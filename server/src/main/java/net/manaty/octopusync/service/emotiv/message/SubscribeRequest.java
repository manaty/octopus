package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@JsonTypeName(JSONRPC.METHOD_SUBSCRIBE)
public class SubscribeRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public SubscribeRequest() {
        // for Jackson
    }

    public SubscribeRequest(long id, String authzToken, Set<String> streams, String sessionId) {
        super(id, buildParams(authzToken, streams, sessionId));
    }

    private static Map<String, Object> buildParams(String authzToken, Set<String> streams, String sessionId) {
        Map<String, Object> params = new HashMap<>((int)(4 / 0.75d + 1));
        params.put("cortexToken", Objects.requireNonNull(authzToken));
        params.put("streams", Objects.requireNonNull(streams));
        params.put("session", Objects.requireNonNull(sessionId));
        return params;
    }
}
