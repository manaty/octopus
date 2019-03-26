package net.manaty.octopusync.di;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.config.PolymorphicConfiguration;
import net.manaty.octopusync.service.s2s.NodeListFactory;

@BQConfig
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface NodeListFactoryConfiguration extends PolymorphicConfiguration {

    NodeListFactory getNodeListFactory(Injector injector);
}
