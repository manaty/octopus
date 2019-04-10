package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collections;

@JsonTypeName(JSONRPC.METHOD_LOGOUT)
public class LogoutRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public LogoutRequest() {
        // for Jackson
    }

    public LogoutRequest(long id, String username) {
        super(id, Collections.singletonMap("username", username));
    }
}
