package net.manaty.octopusync.service;

import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;

import java.util.Set;

public interface EventListener {

    void onS2STimeSyncResult(S2STimeSyncResult r);

    void onClientSessionCreated(String headsetId);

    void onClientTimeSyncResult(ClientTimeSyncResult r);

    void onClientStateUpdate(MoodState moodState);

    void onKnownHeadsetsUpdated(Set<String> headsetIds);

    void onConnectedHeadsetsUpdated(Set<String> headsetIds);
}
