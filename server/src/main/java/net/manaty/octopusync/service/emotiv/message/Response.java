package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

public interface Response<T> {

    @JsonProperty("jsonrpc")
    String jsonrpc();

    @JsonProperty("id")
    long id();

    @JsonProperty("result")
    T result();

    @JsonProperty("error")
    ResponseError error();

    class ResponseError {
        private int code;
        private String message;

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

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("code", code)
                    .add("message", message)
                    .toString();
        }
    }
}
