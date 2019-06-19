package net.manaty.octopusync.service.emotiv.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.manaty.octopusync.model.MotEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEventDecoder;
import net.manaty.octopusync.service.emotiv.message.SubscribeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MotEventDecoderFactory implements CortexEventDecoderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(MotEventDecoderFactory.class);

    private final ObjectMapper mapper;

    public MotEventDecoderFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public CortexEventDecoder createDecoder(SubscribeResponse.StreamInfo streamInfo) {
        List<BiConsumer<MotEvent, JsonNode>> valueSetters = new ArrayList<>();
        streamInfo.visitColumns(new SubscribeResponse.StreamInfo.ColumnInfoVisitor() {
            @Override
            public void visitScalarColumn(String name) {
                switch (name) {
                    case "COUNTER": {
                        valueSetters.add((event, node) -> event.setCounter(node.longValue()));
                        break;
                    }
                    case "GYROX": {
                        valueSetters.add((event, node) -> event.setGyrox(node.doubleValue()));
                        break;
                    }
                    case "GYROY": {
                        valueSetters.add((event, node) -> event.setGyroy(node.doubleValue()));
                        break;
                    }
                    case "GYROZ": {
                        valueSetters.add((event, node) -> event.setGyroz(node.doubleValue()));
                        break;
                    }
                    case "ACCX": {
                        valueSetters.add((event, node) -> event.setAccx(node.doubleValue()));
                        break;
                    }
                    case "ACCY": {
                        valueSetters.add((event, node) -> event.setAccy(node.doubleValue()));
                        break;
                    }
                    case "ACCZ": {
                        valueSetters.add((event, node) -> event.setAccz(node.doubleValue()));
                        break;
                    }
                    case "MAGX": {
                        valueSetters.add((event, node) -> event.setMagx(node.doubleValue()));
                        break;
                    }
                    case "MAGY": {
                        valueSetters.add((event, node) -> event.setMagy(node.doubleValue()));
                        break;
                    }
                    case "MAGZ": {
                        valueSetters.add((event, node) -> event.setMagz(node.doubleValue()));
                        break;
                    }
                    default: {
                        LOGGER.warn("Unsupported event attribute, will not decode: " + name);
                        break;
                    }
                }
            }

            @Override
            public void visitColumnSublist(List<String> names) {
                throw new IllegalStateException("No sublists expected in EEG stream column info");
            }
        });

        return (message) -> {
            try {
                JsonNode node = mapper.readTree(message);

                ArrayNode values = (ArrayNode) node.get("mot");
                if (values == null) {
                    return null;
                }

                MotEvent event = new MotEvent();
                event.setSid(node.get("sid").textValue());
                // time is in seconds since Emotiv app startup
                // https://emotiv.github.io/cortex-docs/#event
                // long emotivTime = (long) (node.get("time").doubleValue() * 1_000);
                // Let's use local server's time instead
                event.setTime(System.currentTimeMillis());

                for (int i = 0; i < values.size(); i++) {
                    valueSetters.get(i).accept(event, values.get(i));
                }

                return event;

            } catch (Exception e) {
                throw new RuntimeException("Failed to decode MOT message: " + message, e);
            }
        };
    }
}
