package net.manaty.octopusync.service;

import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;

import java.util.Set;

public interface EventListener {

    default void onS2STimeSyncResult(S2STimeSyncResult r) {}

    default void onClientSessionCreated(String headsetId) {}

    default void onClientTimeSyncResult(ClientTimeSyncResult r) {}

    default void onClientStateUpdate(MoodState moodState) {}

    default void onKnownHeadsetsUpdated(Set<String> headsetIds) {}

    default void onConnectedHeadsetsUpdated(Set<String> headsetIds) {}

    default void onEegEvent(EegEvent e) {}

    default void onExperienceStarted() {}

    default void onExperienceStopped() {}
}
