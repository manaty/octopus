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
        new RunningMedian(0);
    }

    @Test
    public void testRunningMedian() {
        RunningMedian rm = new RunningMedian(10);

        List<Integer> values = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        Collections.shuffle(values);
        values.forEach(rm::add);

        assertEquals(values.size(), rm.size());
        assertEquals(5, rm.median());

        rm.add(11);
        assertEquals(6, rm.median());

        rm.add(100);
        assertEquals(7, rm.median());
    }
}
