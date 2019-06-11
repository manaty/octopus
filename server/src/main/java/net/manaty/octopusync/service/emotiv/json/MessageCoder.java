package net.manaty.octopusync.service.emotiv.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.manaty.octopusync.service.emotiv.event.CortexEventDecoder;
import net.manaty.octopusync.service.emotiv.event.CortexEventKind;
import net.manaty.octopusync.service.emotiv.message.Request;
import net.manaty.octopusync.service.emotiv.message.Response;
import net.manaty.octopusync.service.emotiv.message.SubscribeResponse;

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
        decoderFactories.put(CortexEventKind.EEG, new EegEventDecoderFactory(mapper));
        decoderFactories.put(CortexEventKind.DEV, new DevEventDecoderFactory(mapper));
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

    public CortexEventDecoder createEventDecoder(CortexEventKind eventKind, SubscribeResponse.StreamInfo streamInfo) {
        return Objects.requireNonNull(decoderFactories.get(eventKind), "Unsupported event kind: " + eventKind)
                .createDecoder(streamInfo);
    }

    /**
     * @return Warning text, if the message is a warning  (e.g. that a session has been stopped)
     */
    // {
    //   "jsonrpc":"2.0",
    //   "warning":{
    //     "code":0,
    //     "message":"All subscriptions of session 5d093b34-09b8-48cd-9169-422ef0e6da70 was stopped by Cortex"
    //   }
    // }
    public @Nullable String lookupWarning(String messageText) throws Exception {
        try {
            JsonNode node = mapper.readTree(messageText);
            if (node.has("warning")) {
                return Objects.requireNonNull(node.get("warning").get("message").textValue());
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception("Failed to lookup warning text in message: " + messageText, e);
        }
    }
}
