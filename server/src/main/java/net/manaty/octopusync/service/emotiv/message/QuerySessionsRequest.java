package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.base.MoreObjects;

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
        params.put("_auth", Objects.requireNonNull(authzToken));
        params.put("query", Query.appId(appId));
        return params;
    }

    public static class Query {

        public static Query appId(String appId) {
            return new Query(appId);
        }

        private final String appId;

        private Query(String appId) {
            this.appId = Objects.requireNonNull(appId);
        }

        public String getAppId() {
            return appId;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("appId", appId)
                    .toString();
        }
    }
}
