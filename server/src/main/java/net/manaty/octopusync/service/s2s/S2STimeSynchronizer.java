package net.manaty.octopusync.service.s2s;

import io.grpc.ManagedChannel;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.processors.PublishProcessor;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;
import net.manaty.octopusync.api.SyncTimeResponse;
import net.manaty.octopusync.model.S2STimeSyncResult;
import net.manaty.octopusync.s2s.api.OctopuSyncS2SGrpc;
import net.manaty.octopusync.s2s.api.OctopuSyncS2SGrpc.OctopuSyncS2SVertxStub;
import net.manaty.octopusync.service.grpc.ManagedChannelFactory;
import net.manaty.octopusync.service.sync.SyncRequestResponseExchange;
import net.manaty.octopusync.service.sync.SyncRequestResponseExchangeFactory;
import net.manaty.octopusync.service.sync.Synchronizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class S2STimeSynchronizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(S2STimeSynchronizer.class);

    private final Vertx vertx;
    private final Supplier<InetSocketAddress> masterServerAddressFactory;
    private final ManagedChannelFactory channelFactory;
    private final Duration masterLookupInterval;
    private final Duration masterSyncInterval;
    private final double devThreshold;
    private final int minSamples;
    private final int maxSamples;
    private final InetSocketAddress localAddress;

    private final ConcurrentMap<InetSocketAddress, Synchronizer<S2STimeSyncResult>> synchronizersByNode;

    private boolean started;
    private long timerId;
    private PublishProcessor<S2STimeSyncResult> resultProcessor;

    public S2STimeSynchronizer(
            Vertx vertx,
            Supplier<InetSocketAddress> masterServerAddressFactory,
            ManagedChannelFactory channelFactory,
            Duration masterLookupInterval,
            Duration masterSyncInterval,
            double devThreshold,
            int minSamples,
            int maxSamples,
            InetSocketAddress localAddress) {

        this.vertx = vertx;
        this.masterServerAddressFactory = masterServerAddressFactory;
        this.channelFactory = channelFactory;
        this.masterLookupInterval = masterLookupInterval;
        this.masterSyncInterval = masterSyncInterval;
        this.devThreshold = devThreshold;
        this.minSamples = minSamples;
        this.maxSamples = maxSamples;
        this.localAddress = localAddress;
        this.synchronizersByNode = new ConcurrentHashMap<>();
    }

    public synchronized Observable<S2STimeSyncResult> startSync() {
        if (started) {
            return Observable.fromPublisher(resultProcessor);
        }

        resultProcessor = PublishProcessor.create();
        scheduleSync();
        started = true;
        return Observable.fromPublisher(resultProcessor);
    }

    private synchronized void scheduleSync() {
        scheduleSync(1, masterLookupInterval.toMillis());
    }

    private synchronized void scheduleSync(long delayMillis, long nextDelayMillis) {
        timerId = vertx.setTimer(delayMillis, it -> {
            loadMasterAddress()
                    .flatMapObservable(this::syncNode)
                    .doAfterTerminate(() -> scheduleSync(nextDelayMillis, nextDelayMillis))
                    .forEach(syncResult -> {
                        synchronized (S2STimeSynchronizer.this) {
                            if (started) {
                                resultProcessor.onNext(syncResult);
                            }
                        }
                    });
        });
    }

    private Maybe<InetSocketAddress> loadMasterAddress() {
        return Maybe.fromCallable(() -> {
            InetSocketAddress address = masterServerAddressFactory.get();
            if (address != null && LOGGER.isDebugEnabled()) {
                LOGGER.debug("Loaded master server address: {}", address);
            }
            return address;
        });
    }

    private synchronized Observable<S2STimeSyncResult> syncNode(InetSocketAddress remoteAddress) {
        if (started) {
            return synchronizersByNode.computeIfAbsent(remoteAddress, this::createSynchronizer)
                    .startSync();
        } else {
            return Observable.empty();
        }
    }

    private Synchronizer<S2STimeSyncResult> createSynchronizer(InetSocketAddress remoteAddress) {
        ManagedChannel channel = channelFactory.createPlaintextChannel(remoteAddress.getHostName(), remoteAddress.getPort());
        OctopuSyncS2SVertxStub stub = OctopuSyncS2SGrpc.newVertxStub(channel);
        S2STimeSyncResultBuilder resultBuilder = S2STimeSyncResultBuilder.builder(localAddress, remoteAddress);

        SyncRequestResponseExchangeFactory exchangeFactory = (handler, exceptionHandler) ->
                getExchange(stub, handler, exceptionHandler);

        return new Synchronizer<>(exchangeFactory, resultBuilder, masterSyncInterval,
                devThreshold, minSamples, maxSamples);
    }

    private SyncRequestResponseExchange getExchange(
            OctopuSyncS2SVertxStub stub,
            Consumer<SyncTimeResponse> handler,
            Consumer<Throwable> exceptionHandler) {

        Future<SyncRequestResponseExchange> future = Future.future();
        stub.syncTime(exchange -> {
            exchange.handler(handler::accept);
            exchange.exceptionHandler(exceptionHandler::accept);
            future.complete(SyncRequestResponseExchange.wrap(exchange));
        });
        return future.rxSetHandler().blockingGet();
    }

    public synchronized void stopSync() {
        vertx.cancelTimer(timerId);
        synchronizersByNode.values().forEach(Synchronizer::stopSync);
        resultProcessor.onComplete();
        resultProcessor = null;
        started = false;
    }
}

