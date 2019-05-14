package net.manaty.octopusync.service.web.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import java.util.Objects;

public class JsonEncoder<T> implements Encoder.Text<T> {

    private EndpointConfig config;

    @Override
    public void init(EndpointConfig config) {
        this.config = config;
    }

    @Override
    public String encode(T object) throws EncodeException {
        ObjectMapper mapper = (ObjectMapper) Objects.requireNonNull(
                config.getUserProperties().get(AdminEndpoint.JSON_MAPPER_PROPERTY));
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new EncodeException(object, "Failed to encode object", e);
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
