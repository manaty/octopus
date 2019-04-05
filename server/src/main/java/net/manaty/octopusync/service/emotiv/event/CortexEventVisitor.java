package net.manaty.octopusync.service.emotiv.event;

public interface CortexEventVisitor {

    void visitEegEvent(EegEvent event);
}
