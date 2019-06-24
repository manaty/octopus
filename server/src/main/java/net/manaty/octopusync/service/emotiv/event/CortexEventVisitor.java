package net.manaty.octopusync.service.emotiv.event;

import net.manaty.octopusync.model.DevEvent;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MotEvent;

public interface CortexEventVisitor {

    void visitEegEvent(EegEvent event);

    void visitDevEvent(DevEvent event);

    void visitMotEvent(MotEvent event);
}
