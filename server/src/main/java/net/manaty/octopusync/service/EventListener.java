package net.manaty.octopusync.service;

import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;

public interface EventListener {

    void onS2STimeSyncResult(S2STimeSyncResult r);

    void onClientSessionCreated(String headsetId);

    void onClientTimeSyncResult(ClientTimeSyncResult r);

    void onClientStateUpdate(MoodState moodState);
}
