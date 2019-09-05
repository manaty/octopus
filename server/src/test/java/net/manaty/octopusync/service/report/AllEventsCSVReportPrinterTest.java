package net.manaty.octopusync.service.report;

import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.Timestamped;
import net.manaty.octopusync.model.Trigger;
import org.junit.Test;

import java.io.StringWriter;
import java.util.*;

import static org.testng.Assert.assertEquals;

public class AllEventsCSVReportPrinterTest {

    @Test
    public void testAllEventsCSVReportPrinter() {
        Map<Class<?>, Iterator<? extends Timestamped>> iteratorMap = new HashMap<>();

        EegEvent e1 = new EegEvent();
        e1.setCounter(1);
        e1.setTime(100);
        e1.setTimeLocal(e1.getTime());
        EegEvent e2 = new EegEvent();
        e2.setCounter(2);
        e2.setTime(200);
        e2.setTimeLocal(e2.getTime());
        EegEvent e3 = new EegEvent();
        e3.setCounter(3);
        e3.setTime(300);
        e3.setTimeLocal(e3.getTime());
        EegEvent e4 = new EegEvent();
        e4.setCounter(4);
        e4.setTime(1000);
        e4.setTimeLocal(e4.getTime());
        iteratorMap.put(EegEvent.class, Arrays.asList(e1, e2, e3, e4).iterator());

        Trigger t1 = Trigger.message(1, 50, "t1"); // skipped
        Trigger t2 = Trigger.message(2, 150, "t2"); // merged into e2
        Trigger t3 = Trigger.message(3, 500, "t3"); // printed separately
        Trigger t4 = Trigger.message(4, 800, "t4"); // merged into e4
        iteratorMap.put(Trigger.class, Arrays.asList(t1, t2, t3, t4).iterator());

        ReportEventProcessor processor = new ReportEventProcessor(iteratorMap);
        AllEventsCSVReportPrinter printer = new AllEventsCSVReportPrinter(processor, false,false);

        String expected =
                "0;100;100;1;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;0;;\n" +
                "0;200;200;2;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;0;;t2\n" +
                "0;300;300;3;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;0;;\n" +
                ";500;;;;;;;;;;;;;;;;;;;;;0;;t3\n" +
                "0;1000;1000;4;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;0;;t4\n";

        assertEquals(expected, printToString(printer));
    }

    private static String printToString(AllEventsCSVReportPrinter printer) {
        StringWriter w = new StringWriter();
        printer.print(w);
        return w.toString();
    }
}
