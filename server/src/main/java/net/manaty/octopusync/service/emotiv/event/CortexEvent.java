package net.manaty.octopusync.service.emotiv.event;

public interface CortexEvent {

    void visitEvent(CortexEventVisitor visitor);
}
