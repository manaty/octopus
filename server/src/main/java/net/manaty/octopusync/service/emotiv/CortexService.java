package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.reactivex.Observable;
import net.manaty.octopusync.service.emotiv.event.CortexEvent;

public interface CortexService {

    Observable<CortexEvent> startCapture();

    Completable stopCapture();
}
