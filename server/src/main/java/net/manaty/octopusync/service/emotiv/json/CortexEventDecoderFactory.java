package net.manaty.octopusync.service.emotiv.json;

import net.manaty.octopusync.service.emotiv.event.CortexEventDecoder;
import net.manaty.octopusync.service.emotiv.message.SubscribeResponse;

public interface CortexEventDecoderFactory {

    CortexEventDecoder createDecoder(SubscribeResponse.StreamInfo streamInfo);
}
