package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collections;

@JsonTypeName(JSONRPC.METHOD_GETUSERLOGIN)
public class GetUserLoginRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public GetUserLoginRequest() {
        // for Jackson
    }

    public GetUserLoginRequest(long id) {
        super(id, Collections.emptyMap());
    }
}
