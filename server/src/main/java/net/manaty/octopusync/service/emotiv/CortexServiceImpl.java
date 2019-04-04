package net.manaty.octopusync.service.emotiv;

import io.reactivex.Completable;
import io.vertx.reactivex.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CortexServiceImpl implements CortexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CortexServiceImpl.class);

    private final Map<String, String> headsetIdsToCodes;
    private final CortexAuthenticator authenticator;

    public CortexServiceImpl(
            Vertx vertx,
            CortexClient client,
            EmotivCredentials credentials,
            Map<String, String> headsetIdsToCodes) {

        CortexAuthenticator authenticator = new CortexAuthenticator(vertx, client, credentials, headsetIdsToCodes.size());
//        authenticator.onNewAuthzTokenIssued();
        this.authenticator = authenticator;

        this.headsetIdsToCodes = headsetIdsToCodes;
    }

    @Override
    public Completable startCapture() {
        return Completable.concatArray(authenticator.start());
    }

    @Override
    public Completable stopCapture() {
        return Completable.concatArray(authenticator.stop());
    }


}
