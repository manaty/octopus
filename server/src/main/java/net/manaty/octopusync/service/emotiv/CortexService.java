package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;

public interface CortexService {
    Completable startCapture();

    Completable stopCapture();
}
