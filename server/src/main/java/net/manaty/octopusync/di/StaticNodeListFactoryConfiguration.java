package net.manaty.octopusync.di;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import net.manaty.octopusync.service.common.NetworkUtils;
import net.manaty.octopusync.service.s2s.NodeListFactory;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@BQConfig
@JsonTypeName("static")
public class StaticNodeListFactoryConfiguration implements NodeListFactoryConfiguration {

    private List<String> addresses;

    public StaticNodeListFactoryConfiguration() {
        this.addresses = Collections.emptyList();
    }

    @BQConfigProperty("List of node addresses in format <hostname|ipaddress>:<port>")
    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    @Override
    public NodeListFactory getNodeListFactory(Injector injector) {
        if (addresses.isEmpty()) {
            throw new IllegalStateException("Missing addresses");
        }
        List<InetSocketAddress> list = addresses.stream()
                .map(NetworkUtils::parseAddress)
                .collect(Collectors.toList());
        return () -> list;
    }
}
