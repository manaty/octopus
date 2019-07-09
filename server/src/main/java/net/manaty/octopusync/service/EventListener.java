package net.manaty.octopusync.service;

import net.manaty.octopusync.model.*;

import java.net.InetAddress;
import java.util.Set;

public interface EventListener {

    default void onSlaveServerConnected(InetAddress address) {};

    default void onS2STimeSyncResult(S2STimeSyncResult r) {}

    default void onClientConnectionCreated(String headsetId) {}

    default void onClientConnectionTerminated(String headsetId) {}

    default void onClientTimeSyncResult(ClientTimeSyncResult r) {}

    default void onClientStateUpdate(MoodState moodState) {}

    default void onKnownHeadsetsUpdated(Set<String> headsetIds) {}

    default void onConnectedHeadsetsUpdated(Set<String> headsetIds) {}

    default void onEegEvent(EegEvent e) {}

    default void onDevEvent(DevEvent event) {}

    default void onExperienceStarted() {}

    default void onExperienceStopped() {}

    default void onAdminTrigger(Trigger trigger) {}

    default void onMusicOn() {}

    default void onMusicOff() {}
}
