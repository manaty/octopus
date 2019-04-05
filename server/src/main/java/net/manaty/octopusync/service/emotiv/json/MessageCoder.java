package net.manaty.octopusync.service.emotiv.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.manaty.octopusync.service.emotiv.event.CortexEventDecoder;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.message.Request;
import net.manaty.octopusync.service.emotiv.message.Response;

import javax.annotation.Nullable;
import java.util.*;

public class MessageCoder {

    private final ObjectMapper mapper;
    private final Map<CortexEventKind, CortexEventDecoderFactory> decoderFactories;

    public MessageCoder() {
        this.mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.decoderFactories = buildDecoderFactories(mapper);
    }

    private Map<CortexEventKind, CortexEventDecoderFactory> buildDecoderFactories(ObjectMapper mapper) {
        Map<CortexEventKind, CortexEventDecoderFactory> decoderFactories = new HashMap<>();
        // TODO: support other events?
        decoderFactories.put(CortexEventKind.EEG, new EegEventDecoderFactory(mapper));
        return decoderFactories;
    }

    public String encodeRequest(Request request) {
        try {
            return mapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode request: " + request, e);
        }
    }

    public <R extends Response<?>> R decodeResponse(Class<R> responseType, String responseText) throws Exception {
        try {
            return mapper.readValue(responseText, responseType);
        } catch (Exception e) {
            throw new Exception("Failed to decode response of type " +
                    responseType.getName() + ": " + responseText, e);
        }
    }

    /**
     * @return Message ID if it's present or OptionalLong.empty() otherwise
     */
    public OptionalLong lookupMessageId(String messageText) throws Exception {
        try {
            JsonNode node = mapper.readTree(messageText);
            if (node.has("id")) {
                return OptionalLong.of(node.get("id").asLong());
            } else {
                return OptionalLong.empty();
            }
        } catch (Exception e) {
            throw new Exception("Failed to lookup ID in message: " + messageText, e);
        }
    }

    /**
     *
     * @return Subscription ID if the message is an event or null otherwise
     */
    public @Nullable String lookupSubscriptionId(String messageText) throws Exception {
        try {
            JsonNode node = mapper.readTree(messageText);
            if (node.has("sid")) {
                return Objects.requireNonNull(node.get("sid").textValue());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception("Failed to lookup subscription ID in message: " + messageText, e);
        }
    }

    public CortexEventDecoder createEventDecoder(CortexEventKind eventKind, List<String> columns) {
        return Objects.requireNonNull(decoderFactories.get(eventKind), "Unsupported event kind: " + eventKind)
                .createDecoder(columns);
    }
}
