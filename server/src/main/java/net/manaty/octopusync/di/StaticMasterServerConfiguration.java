package net.manaty.octopusync.di;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.LazySupplier;
import net.manaty.octopusync.service.common.NetworkUtils;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

@BQConfig
@JsonTypeName("static")
public class StaticMasterServerConfiguration implements MasterServerConfiguration {

    private String address;

    @BQConfigProperty("Master server address in format <hostname|ipaddress>:<port>")
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public Supplier<InetSocketAddress> getMasterServerAddressFactory(Injector injector) {
        return LazySupplier.lazySupplier(() -> NetworkUtils.parseAddress(address));
    }
}
