package net.manaty.octopusync.service.emotiv;

import io.reactivex.Observable;

public class CortexService {

    private final CortexClient cortexClient;
    private final EmotivCredentials credentials;

    public CortexService(CortexClient cortexClient, EmotivCredentials credentials) {
        this.cortexClient = cortexClient;
        this.credentials = credentials;
    }

    // TODO: should return Events (when the model becomes clear)
    public Observable<Void> startCapture() {
        return Observable.empty();
    }
}
