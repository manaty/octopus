package net.manaty.octopusync.service.emotiv.event;

import javax.annotation.Nullable;

public interface CortexEventDecoder {

    @Nullable CortexEvent decode(String message);
}
