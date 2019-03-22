package net.manaty.octopusync.service.s2s;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.processors.PublishProcessor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.reactivex.core.Future;
import net.manaty.octopusync.model.SyncResult;
import net.manaty.octopusync.s2s.api.OctopuSyncS2SGrpc.OctopuSyncS2SVertxStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Synchronizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Synchronizer.class);

    private final AtomicLong roundNumSeq;
    private final OctopuSyncS2SVertxStub stub;
    private final SyncResultBuilder resultBuilder;
    private final Duration delay;

    private final SyncResultHandler handler;

    private boolean started;
    private PublishProcessor<SyncResult> resultProcessor;

    public Synchronizer(OctopuSyncS2SVertxStub stub, SyncResultBuilder resultBuilder, Duration delay) {
        this.stub = stub;
        this.roundNumSeq = new AtomicLong(1);
        this.resultBuilder = resultBuilder;
        this.delay = delay;
        this.handler = new SyncResultHandler();
    }

    public synchronized Observable<SyncResult> startSync() {
        if (started) {
            return Observable.fromPublisher(resultProcessor);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Starting synchronization with {}", resultBuilder.getRemoteAddress());
        }

        resultProcessor = PublishProcessor.create();
        sync();
        started = true;
        return Observable.fromPublisher(resultProcessor);
    }

    private synchronized void sync() {
        stub.syncTime(exchange -> {
            // callback is executed in the same thread, so we're still in critical section
            Future<SyncResult> future = Future.future();
            future.setHandler(handler);
            new SyncRound(resultBuilder.newBuilderForRound(roundNumSeq.getAndIncrement()), exchange, future)
                    .execute();
        });
    }

    private class SyncResultHandler implements Handler<AsyncResult<SyncResult>> {
        @Override
        public void handle(AsyncResult<SyncResult> ar) {
            synchronized (Synchronizer.this) {
                if (started) {
                    if (ar.succeeded()) {
                        resultProcessor.onNext(ar.result());
                    } else {
                        Throwable cause = ar.cause();
                        LOGGER.error("Unexpected synchronization failure", cause);
                        resultProcessor.onError(cause);
                    }
                    Completable.timer(delay.toMillis(), TimeUnit.MILLISECONDS)
                            .subscribe(Synchronizer.this::sync);
                }
            }
        }
    }

    public synchronized void stopSync() {
        if (started) {
            resultProcessor.onComplete();
            resultProcessor = null;
            started = false;
        }
    }
}