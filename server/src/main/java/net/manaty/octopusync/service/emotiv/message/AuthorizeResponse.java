package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    }
}
