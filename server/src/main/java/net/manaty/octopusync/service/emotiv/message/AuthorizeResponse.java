package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class AuthorizeResponse extends BaseResponse<String> {

    private AuthTokenHolder tokenHolder;

    @Override
    public String result() {
        if (tokenHolder == null) {
            throw new IllegalStateException("Result is absent");
        }
        return tokenHolder.token;
    }

    public void setResult(AuthTokenHolder tokenHolder) {
        this.tokenHolder = tokenHolder;
    }

    public static class AuthTokenHolder {
        private String token;

        @JsonProperty("_auth")
        public void setToken(String token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("token", token)
                    .toString();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tokenHolder", tokenHolder)
                .add("jsonrpc", jsonrpc)
                .add("id", id)
                .add("error", error)
                .toString();
    }
}
