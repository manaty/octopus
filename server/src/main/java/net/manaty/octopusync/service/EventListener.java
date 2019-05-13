package net.manaty.octopusync.service;

import net.manaty.octopusync.model.S2STimeSyncResult;

public interface EventListener {

    void onS2STimeSyncResult(S2STimeSyncResult r);
}
