package net.manaty.octopusync.service.sync;

import net.manaty.octopusync.api.SyncTimeResponse;

import java.util.function.Consumer;

public interface SyncRequestResponseExchangeFactory {

    SyncRequestResponseExchange createExchange(
            Consumer<SyncTimeResponse> responseHandler,
            Consumer<Throwable> exceptionHandler);
}
