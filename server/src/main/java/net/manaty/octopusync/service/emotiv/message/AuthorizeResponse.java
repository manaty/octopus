package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public class AuthorizeResponse extends BaseResponse<AuthorizeResponse.AuthTokenHolder> {

    private AuthTokenHolder tokenHolder;

    @Override
    public AuthTokenHolder result() {
        return tokenHolder;
    }

    public void setResult(AuthTokenHolder tokenHolder) {
        this.tokenHolder = tokenHolder;
    }

    public static class AuthTokenHolder {
        private String token;
        private Warning warning;

        public String getToken() {
            return token;
        }

        @JsonProperty("cortexToken")
        public void setToken(String token) {
            this.token = token;
        }

        public Warning getWarning() {
            return warning;
        }

        public void setWarning(Warning warning) {
            this.warning = warning;
        }

        public static class Warning {
            private int code;
            private String message;
            private String licenseUrl;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getLicenseUrl() {
                return licenseUrl;
            }

            public void setLicenseUrl(String licenseUrl) {
                this.licenseUrl = licenseUrl;
            }

            @Override
            public String toString() {
                return MoreObjects.toStringHelper(this)
                        .add("code", code)
                        .add("message", message)
                        .add("licenseUrl", licenseUrl)
                        .toString();
            }
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
