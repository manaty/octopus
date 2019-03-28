package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Response<T> {

    @JsonProperty("jsonrpc")
    String jsonrpc();

    @JsonProperty("id")
    long id();

    @JsonProperty("result")
    T result();
}
