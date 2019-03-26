package net.manaty.octopusync.service.db;

import io.reactivex.Completable;
import net.manaty.octopusync.model.S2STimeSyncResult;

public interface Storage {

    Completable save(S2STimeSyncResult syncResult);
}
