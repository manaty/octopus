package net.manaty.octopusync.model;

import java.util.Comparator;

public interface Timestamped extends Comparable<Timestamped> {

    Comparator<Timestamped> ASC_COMPARATOR = Comparator.comparingLong(Timestamped::timestamp);

    long timestamp();

    @Override
    default int compareTo(Timestamped o) {
        return ASC_COMPARATOR.compare(this, o);
    }
}
