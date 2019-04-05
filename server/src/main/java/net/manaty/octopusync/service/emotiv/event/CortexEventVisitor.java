package net.manaty.octopusync.service.emotiv.event;

import net.manaty.octopusync.model.EegEvent;

public interface CortexEventVisitor {

    void visitEegEvent(EegEvent event);
}
