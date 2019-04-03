package net.manaty.octopusync.service.emotiv.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.annotation.Nullable;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = PROPERTY, property = "method")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GetUserLoginRequest.class),
        @JsonSubTypes.Type(value = LoginRequest.class),
        @JsonSubTypes.Type(value = AuthorizeRequest.class),
        @JsonSubTypes.Type(value = SubscribeRequest.class)
})
public interface Request {

    @JsonProperty("jsonrpc")
    String jsonrpc();

    @JsonProperty("id")
    long id();

    @JsonProperty("params")
    @Nullable
    Map<String, Object> params();
}
