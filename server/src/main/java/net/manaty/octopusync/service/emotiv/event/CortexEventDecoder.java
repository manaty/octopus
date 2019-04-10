package net.manaty.octopusync.service.emotiv.event;

public interface CortexEventDecoder {

    CortexEvent decode(String message);
}
