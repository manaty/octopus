package net.manaty.octopusync.service.db;

import io.reactivex.Completable;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;

public interface CortexEventPersistor {

    Completable start();

    Completable stop();

    void save(CortexEvent event);
}
