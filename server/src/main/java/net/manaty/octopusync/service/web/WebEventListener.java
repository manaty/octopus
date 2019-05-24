package net.manaty.octopusync.service.web;

import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.model.Trigger;
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
    public void onClientSessionCreated(String headsetId) {
        adminEndpoint.onClientSessionCreated(headsetId);
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
    public void onAdminTrigger(Trigger trigger) {
        storage.saveTrigger(trigger)
                .blockingAwait();
    }
}
