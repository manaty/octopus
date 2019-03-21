package net.manaty.octopusync.service.s2s;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.function.Supplier;

public interface NodeListFactory extends Supplier<List<InetSocketAddress>> {
}
