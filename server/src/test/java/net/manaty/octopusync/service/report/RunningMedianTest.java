package net.manaty.octopusync.service.report;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.testng.Assert.assertEquals;

public class RunningMedianTest {

    @Test(expected = Exception.class)
    public void testRunningMedian_IllegalCapacity() {
        new RunningMedian(0, 0);
    }

    @Test
    public void testRunningMedian() {
        RunningMedian rm = new RunningMedian(10, 0);

        List<Integer> values = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        Collections.shuffle(values);
        values.forEach(rm::add);

        assertEquals(rm.size(), values.size());
        assertEquals(rm.median(), 5);

        rm.add(11);
        assertEquals(rm.median(), 6);

        rm.add(100);
        assertEquals(rm.median(), 7);
    }
}
