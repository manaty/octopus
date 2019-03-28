package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public interface Request {

    @JsonProperty("jsonrpc")
    default String jsonrpc() {
        return "2.0";
    }

    @JsonProperty("id")
    long id();

    @JsonProperty("method")
    String method();

    @JsonProperty("params")
    Map<String, Object> params();
}
