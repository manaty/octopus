package net.manaty.octopusync.service.db;

import io.reactivex.Completable;
import net.manaty.octopusync.model.ClientTimeSyncResult;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface Storage {

    Completable save(S2STimeSyncResult syncResult);

    Stream<S2STimeSyncResult> getS2SSyncResults(long from, long to);

    Completable save(List<EegEvent> events);

    Stream<EegEvent> getEegEvents(String headsetId, long from, long to);

    Completable save(MoodState moodState);

    Stream<MoodState> getMoodStates(String headsetId, long from, long to);

    Completable save(ClientTimeSyncResult syncResult);

    Stream<ClientTimeSyncResult> getClientSyncResults(String headsetId, long from, long to);

    Set<String> getHeadsetIdsFromEegEvents();
}
