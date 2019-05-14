package net.manaty.octopusync.service.web;

import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.service.EventListener;
import net.manaty.octopusync.service.web.admin.AdminEndpoint;

import java.util.Set;

public class WebEventListener implements EventListener {

    private final AdminEndpoint adminEndpoint;

    public WebEventListener(AdminEndpoint adminEndpoint) {
        this.adminEndpoint = adminEndpoint;
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
}
