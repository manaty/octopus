package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collections;

@JsonTypeName(JSONRPC.METHOD_QUERYHEADSETS)
public class QueryHeadsetsRequest extends BaseRequest {

    @SuppressWarnings("unused")
    public QueryHeadsetsRequest() {
        // for Jackson
    }

    public QueryHeadsetsRequest(long id) {
        super(id, Collections.emptyMap());
    }
}
