package net.manaty.octopusync.service.emotiv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.manaty.octopusync.service.emotiv.message.Request;
import net.manaty.octopusync.service.emotiv.message.Response;

import java.io.IOException;

public class MessageCoder {

    private final ObjectMapper mapper;

    public MessageCoder() {
        mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public String encodeRequest(Request request) {
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to encode request: " + request, e);
        }
    }

    public <R extends Response<?>> R decodeResponse(Class<R> responseType, String responseText) throws Exception {
        try {
            return mapper.readValue(responseText, responseType);
        } catch (IOException e) {
            throw new Exception("Failed to decode response of type " +
                    responseType.getName() + ": " + responseText, e);
        }
    }

    public long lookupMessageId(String messageText) throws Exception {
        try {
            return mapper.readTree(messageText).get("id").asLong();
        } catch (IOException e) {
            throw new Exception("Failed to lookup ID in message: " + messageText, e);
        }
    }
}
