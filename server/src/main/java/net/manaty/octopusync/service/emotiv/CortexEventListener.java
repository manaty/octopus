package net.manaty.octopusync.service.emotiv;

import net.manaty.octopusync.service.emotiv.event.CortexEvent;
import net.manaty.octopusync.service.emotiv.message.Response.ResponseError;

public interface CortexEventListener {

    void onEvent(CortexEvent event);

    void onError(ResponseError error);

    void onError(Throwable e);
}
