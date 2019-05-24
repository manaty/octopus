package net.manaty.octopusync.di;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.config.PolymorphicConfiguration;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

@BQConfig("Provides master server's address for server-to-server time synchronization.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = StaticMasterServerConfiguration.class)
public interface MasterServerConfiguration extends PolymorphicConfiguration {

    Supplier<InetSocketAddress> getMasterServerAddressFactory(Injector injector);
}
