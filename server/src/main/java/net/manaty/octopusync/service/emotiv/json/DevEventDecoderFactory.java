package net.manaty.octopusync.service.emotiv.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.manaty.octopusync.model.DevEvent;
import net.manaty.octopusync.service.emotiv.event.CortexEventDecoder;
import net.manaty.octopusync.service.emotiv.message.SubscribeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class DevEventDecoderFactory implements CortexEventDecoderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(DevEventDecoderFactory.class);

    private final ObjectMapper mapper;

    public DevEventDecoderFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public CortexEventDecoder createDecoder(SubscribeResponse.StreamInfo streamInfo) {
        List<BiConsumer<DevEvent, JsonNode>> valueSetters = new ArrayList<>();
        streamInfo.visitColumns(new SubscribeResponse.StreamInfo.ColumnInfoVisitor() {
            @Override
            public void visitScalarColumn(String name) {
                switch (name) {
                    case "Battery": {
                        valueSetters.add((event, node) -> event.setBattery(node.intValue()));
                        break;
                    }
                    case "Signal": {
                        valueSetters.add((event, node) -> event.setSignal(node.intValue()));
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
                List<BiConsumer<DevEvent, JsonNode>> sublistValueSetters = new ArrayList<>();
                names.forEach(name -> {
                    switch (name) {
                        case "AF3": {
                            sublistValueSetters.add((event, node) -> event.setAf3(node.doubleValue()));
                            break;
                        }
                        case "F7": {
                            sublistValueSetters.add((event, node) -> event.setF7(node.doubleValue()));
                            break;
                        }
                        case "F3": {
                            sublistValueSetters.add((event, node) -> event.setF3(node.doubleValue()));
                            break;
                        }
                        case "FC5": {
                            sublistValueSetters.add((event, node) -> event.setFc5(node.doubleValue()));
                            break;
                        }
                        case "T7": {
                            sublistValueSetters.add((event, node) -> event.setT7(node.doubleValue()));
                            break;
                        }
                        case "P7": {
                            sublistValueSetters.add((event, node) -> event.setP7(node.doubleValue()));
                            break;
                        }
                        case "O1": {
                            sublistValueSetters.add((event, node) -> event.setO1(node.doubleValue()));
                            break;
                        }
                        case "O2": {
                            sublistValueSetters.add((event, node) -> event.setO2(node.doubleValue()));
                            break;
                        }
                        case "P8": {
                            sublistValueSetters.add((event, node) -> event.setP8(node.doubleValue()));
                            break;
                        }
                        case "T8": {
                            sublistValueSetters.add((event, node) -> event.setT8(node.doubleValue()));
                            break;
                        }
                        case "FC6": {
                            sublistValueSetters.add((event, node) -> event.setFc6(node.doubleValue()));
                            break;
                        }
                        case "F4": {
                            sublistValueSetters.add((event, node) -> event.setF4(node.doubleValue()));
                            break;
                        }
                        case "F8": {
                            sublistValueSetters.add((event, node) -> event.setF8(node.doubleValue()));
                            break;
                        }
                        case "AF4": {
                            sublistValueSetters.add((event, node) -> event.setAf4(node.doubleValue()));
                            break;
                        }
                        default: {
                            LOGGER.warn("Unsupported event attribute, will not decode: " + name);
                            break;
                        }
                    }
                });

                valueSetters.add((event, arrayNode) -> {
                    for (int i = 0; i < sublistValueSetters.size(); i++) {
                        sublistValueSetters.get(i).accept(event, arrayNode.get(i));
                    }
                });
            }
        });

        return (message) -> {
            try {
                JsonNode node = mapper.readTree(message);

                ArrayNode values = (ArrayNode) node.get("dev");
                if (values == null) {
                    return null;
                }

                DevEvent event = new DevEvent();
                event.setSid(node.get("sid").textValue());
                // time is in seconds since Emotiv app startup
                // https://emotiv.github.io/cortex-docs/#event
                // long emotivTime = (long) (node.get("time").doubleValue() * 1_000);
                // Let's use local server's time instead
                event.setTime(System.currentTimeMillis());

                for (int i = 0; i < valueSetters.size(); i++) {
                    valueSetters.get(i).accept(event, values.get(i));
                }

                return event;

            } catch (Exception e) {
                throw new RuntimeException("Failed to decode DEV message: " + message, e);
            }
        };
    }
}
