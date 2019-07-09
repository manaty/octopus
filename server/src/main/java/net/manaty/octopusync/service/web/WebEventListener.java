package net.manaty.octopusync.service.web;

import net.manaty.octopusync.model.*;
import net.manaty.octopusync.service.EventListener;
import net.manaty.octopusync.service.db.Storage;
import net.manaty.octopusync.service.web.ws.AdminEndpoint;

import java.net.InetAddress;
import java.util.Set;

public class WebEventListener implements EventListener {

    private final AdminEndpoint adminEndpoint;
    private final Storage storage;

    public WebEventListener(AdminEndpoint adminEndpoint, Storage storage) {
        this.adminEndpoint = adminEndpoint;
        this.storage = storage;
    }

    @Override
    public void onSlaveServerConnected(InetAddress address) {
        adminEndpoint.onSlaveServerConnected(address);
    }

    @Override
    public void onS2STimeSyncResult(S2STimeSyncResult r) {
        adminEndpoint.onS2STimeSyncResult(r);
    }

    @Override
    public void onClientConnectionCreated(String headsetId) {
        adminEndpoint.onClientConnectionCreated(headsetId);
    }

    @Override
    public void onClientConnectionTerminated(String headsetId) {
        adminEndpoint.onClientConnectionTerminated(headsetId);
    }

    @Override
    public void onClientTimeSyncResult(ClientTimeSyncResult r) {
        adminEndpoint.onClientTimeSyncResult(r);
    }

    @Override
    public void onClientStateUpdate(MoodState moodState) {
        adminEndpoint.onClientStateUpdate(moodState);
    }

    @Override
    public void onKnownHeadsetsUpdated(Set<String> headsetIds) {
        adminEndpoint.onKnownHeadsetsUpdated(headsetIds);
    }

    @Override
    public void onConnectedHeadsetsUpdated(Set<String> headsetIds) {
        adminEndpoint.onConnectedHeadsetsUpdated(headsetIds);
    }

    @Override
    public void onDevEvent(DevEvent event) {
        adminEndpoint.onDevEvent(event);
    }

    @Override
    public void onAdminTrigger(Trigger trigger) {
        saveTriggerBlocking(trigger);
    }

    @Override
    public void onMusicOn() {
        Trigger trigger = Trigger.musicOn(0, System.currentTimeMillis());
        saveTriggerBlocking(trigger);
    }

    @Override
    public void onMusicOff() {
        Trigger trigger = Trigger.musicOff(0, System.currentTimeMillis());
        saveTriggerBlocking(trigger);
    }

    private void saveTriggerBlocking(Trigger trigger) {
        storage.saveTrigger(trigger)
                .blockingAwait();
    }
}
