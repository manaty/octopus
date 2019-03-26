package net.manaty.octopusync.service.s2s;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@FunctionalInterface
public interface NodeListFactory extends Supplier<List<InetSocketAddress>> {

    default List<InetSocketAddress> map(UnaryOperator<InetSocketAddress> mapper) {
        return get().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
}
