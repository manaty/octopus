package net.manaty.octopusync.service;

import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class TimestampUpdatingEventListener implements EventListener {

    private final AtomicLong masterServerDelay;
    private final ConcurrentMap<String, Long> clientDelaysByHeadsetId;

    public TimestampUpdatingEventListener() {
        this.masterServerDelay = new AtomicLong(0);
        this.clientDelaysByHeadsetId = new ConcurrentHashMap<>();
    }

    @Override
    public void onS2STimeSyncResult(S2STimeSyncResult r) {
        masterServerDelay.set(r.getDelay());
    }

    @Override
    public void onClientTimeSyncResult(ClientTimeSyncResult r) {
        clientDelaysByHeadsetId.put(r.getHeadsetId(), r.getDelay());
    }

    @Override
    public void onClientStateUpdate(MoodState moodState) {
        long since = moodState.getSinceTimeUtc();
        long delay = clientDelaysByHeadsetId.getOrDefault(moodState.getHeadsetId(), 0L);
        moodState.setSinceTimeUtc(since - delay + masterServerDelay.get());
    }

    @Override
    public void onEegEvent(EegEvent e) {
        e.setTime(e.getTime() + masterServerDelay.get());
    }
}
