package net.manaty.octopusync.service.emotiv.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.manaty.octopusync.service.emotiv.event.CortexEventDecoder;
import net.manaty.octopusync.service.emotiv.event.EegEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class EegEventDecoderFactory implements CortexEventDecoderFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(EegEventDecoderFactory.class);

    private final ObjectMapper mapper;

    public EegEventDecoderFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public CortexEventDecoder createDecoder(List<String> columns) {
        List<BiConsumer<EegEvent, JsonNode>> valueSetters = new ArrayList<>(columns.size() + 1);
        columns.forEach(column -> {
            switch (column) {
                case "IED_COUNTER": {
                    valueSetters.add((event, node) -> event.setCounter(node.longValue()));
                    break;
                }
                case "IED_INTERPOLATED": {
                    valueSetters.add((event, node) -> event.setInterpolated(node.intValue() == 1));
                    break;
                }
                case "IED_RAW_CQ": {
                    valueSetters.add((event, node) -> event.setSignalQuality(node.doubleValue()));
                    break;
                }
                case "IED_AF3": {
                    valueSetters.add((event, node) -> event.setAf3(node.doubleValue()));
                    break;
                }
                case "IED_T7": {
                    valueSetters.add((event, node) -> event.setT7(node.doubleValue()));
                    break;
                }
                case "IED_Pz": {
                    valueSetters.add((event, node) -> event.setPz(node.doubleValue()));
                    break;
                }
                case "IED_T8": {
                    valueSetters.add((event, node) -> event.setT8(node.doubleValue()));
                    break;
                }
                case "IED_AF4": {
                    valueSetters.add((event, node) -> event.setAf4(node.doubleValue()));
                    break;
                }
                case "IED_MARKER_HARDWARE": {
                    valueSetters.add((event, node) -> event.setMarkerHardware(node.intValue()));
                    break;
                }
                case "IED_MARKER": {
                    valueSetters.add((event, node) -> event.setMarker(node.intValue()));
                    break;
                }
                default: {
                    LOGGER.warn("Unsupported event attribute, will not decode: " + column);
                    break;
                }
            }
        });
        return (message) -> {
            try {
                JsonNode node = mapper.readTree(message);

                EegEvent event = new EegEvent();
                event.setSid(node.get("sid").textValue());
                event.setTime(node.get("time").doubleValue());

                ArrayNode values = (ArrayNode) node.get("eeg");
                for (int i = 0; i < values.size(); i++) {
                    valueSetters.get(i).accept(event, values.get(i));
                }

                return event;

            } catch (Exception e) {
                throw new RuntimeException("Failed to decode EEG message: " + message, e);
            }
        };
    }
}
