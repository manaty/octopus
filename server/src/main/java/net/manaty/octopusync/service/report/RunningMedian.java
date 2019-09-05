package net.manaty.octopusync.service.report;

import java.util.Iterator;
import java.util.TreeSet;

public class RunningMedian {

    /**
     * Stores N last seen _distinct_ values
     */
    private final TreeSet<Long> valuesWindow;
    private final int capacity;

    private final long defaultMedian;

    public RunningMedian(int capacity, long defaultMedian) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Invalid capacity: " + capacity);
        }
        this.capacity = capacity;
        this.valuesWindow = new TreeSet<>();
        this.defaultMedian = defaultMedian;
    }

    public void add(long value) {
        if (valuesWindow.size() == capacity) {
            valuesWindow.pollFirst();
        }
        valuesWindow.add(value);
    }

    public long median() {
        int size = size();
        if (size == 0) {
            return defaultMedian;
        }
        Iterator<Long> iter = valuesWindow.iterator();
        int position = 0;
        while (position < (size / 2 - 1)) {
            position++;
            iter.next();
        }
        return iter.next();
    }

    public final int size() {
        return valuesWindow.size();
    }
}
