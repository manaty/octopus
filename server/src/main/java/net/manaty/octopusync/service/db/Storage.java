package net.manaty.octopusync.service.db;

import io.reactivex.Completable;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.S2STimeSyncResult;

import java.util.List;

public interface Storage {

    Completable save(S2STimeSyncResult syncResult);

    Completable save(List<EegEvent> events);

    Completable save(MoodState moodState);
}
