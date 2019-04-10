package net.manaty.octopusync.service.emotiv.json;

import net.manaty.octopusync.service.emotiv.event.CortexEventDecoder;

import java.util.List;

public interface CortexEventDecoderFactory {

    CortexEventDecoder createDecoder(List<String> columns);
}
