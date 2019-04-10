package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collections;

@JsonTypeName(JSONRPC.METHOD_QUERYSESSIONS)
public class QuerySessionsRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public QuerySessionsRequest() {
        // for Jackson
    }

    public QuerySessionsRequest(long id, String authzToken) {
        super(id, Collections.singletonMap("_auth", authzToken));
    }
}
