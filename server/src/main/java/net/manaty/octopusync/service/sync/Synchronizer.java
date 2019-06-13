package net.manaty.octopusync.service.sync;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.processors.PublishProcessor;
import io.vertx.core.AsyncResult;
import io.vertx.reactivex.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class Synchronizer<R> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Synchronizer.class);

    private final AtomicLong roundNumSeq;
    private final SyncRequestResponseExchangeFactory exchangeFactory;
    private final SyncResultBuilder<?, R> resultBuilder;
    private final Duration delayBetweenRounds;

    private boolean started;
    private PublishProcessor<R> resultProcessor;

    public Synchronizer(
            SyncRequestResponseExchangeFactory exchangeFactory,
            SyncResultBuilder<?, R> resultBuilder,
            Duration delayBetweenRounds) {

        this.exchangeFactory = exchangeFactory;
        this.roundNumSeq = new AtomicLong(1);
        this.resultBuilder = resultBuilder;
        this.delayBetweenRounds = delayBetweenRounds;
    }

    public synchronized Observable<R> startSync() {
        if (started) {
            return Observable.fromPublisher(resultProcessor);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Starting synchronization with {}", resultBuilder.getTargetDescription());
        }

        resultProcessor = PublishProcessor.create();
        sync();
        started = true;
        return Observable.fromPublisher(resultProcessor);
    }

    private synchronized void sync() {
        Future<R> future = Future.future();

        SyncResultBuilder<?, R> roundResultBuilder = resultBuilder.newBuilderForRound(roundNumSeq.getAndIncrement());
        SyncRound<R> round = new SyncRound<>(roundResultBuilder, future::complete);

        SyncRequestResponseExchange exchange = exchangeFactory.createExchange(
                response -> {
                    try {
                        round.handleResponse(response);
                    } catch (Exception e) {
                        if (future.isComplete()) {
                            LOGGER.error("Exception while handling other party's response" +
                                    " (can't fail the future as it is already complete): ", e);
                        } else {
                            future.fail(e);
                        }
                    }
                }, e -> {
                    // check for completion as we might intentionally fail the exchange
                    // after successfully finishing sync round
                    if (!future.isComplete()) {
                        future.complete(roundResultBuilder.failure(System.currentTimeMillis(), e));
                    }
                });

        future.setHandler(ar -> handleSyncResult(ar, exchange));
        round.execute(request -> {
            try {
                exchange.write(request);
            } catch (Exception e) {
                future.fail(new Exception("Failed to write to bidi exchange", e));
            }
        });
    }

    private synchronized void handleSyncResult(AsyncResult<R> ar, SyncRequestResponseExchange exchange) {
        synchronized (Synchronizer.this) {
            if (started) {
                if (ar.succeeded()) {
                    exchange.end();
                    resultProcessor.onNext(ar.result());
                } else {
                    exchange.fail(ar.cause());
                    resultProcessor.onError(ar.cause());
                }
                Completable.timer(delayBetweenRounds.toMillis(), TimeUnit.MILLISECONDS)
                        .subscribe(Synchronizer.this::sync);
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