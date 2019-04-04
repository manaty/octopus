package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonTypeName(JSONRPC.METHOD_UPDATESESSION)
public class UpdateSessionRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public UpdateSessionRequest() {
        // for Jackson
    }

    public UpdateSessionRequest(long id, String authzToken, String session, String status) {
        super(id, buildParams(authzToken, session, status));
    }

    private static Map<String, Object> buildParams(String authzToken, String session, String status) {
        Map<String, Object> params = new HashMap<>((int)(3 / 0.75d + 1));
        params.put("_auth", Objects.requireNonNull(authzToken));
        params.put("session", Objects.requireNonNull(session));
        params.put("status", Objects.requireNonNull(status));
        return params;
    }
}
