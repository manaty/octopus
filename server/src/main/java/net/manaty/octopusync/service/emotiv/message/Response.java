package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    }
}
