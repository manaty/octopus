package net.manaty.octopusync.service.report;

import net.manaty.octopusync.api.State;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.Timestamped;
import net.manaty.octopusync.model.Trigger;
import org.junit.Test;

import java.io.StringWriter;
import java.util.*;

import static org.testng.Assert.assertEquals;

public class AllEventsCSVReportPrinterTest {

    @Test
    public void testAllEventsCSVReportPrinter_Triggers() {
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

        Trigger t1 = Trigger.message(1, 0, "t1"); // printed separately
        Trigger t2 = Trigger.message(1, 50, "t2"); // printed separately
        Trigger t3 = Trigger.message(2, 150, "t3"); // merged into e2
        Trigger t4 = Trigger.message(3, 500, "t4"); // printed separately
        Trigger t5 = Trigger.message(4, 800, "t5"); // merged into e4
        Trigger t6 = Trigger.message(1, 1050, "t6"); // printed separately
        Trigger t7 = Trigger.message(1, 1150, "t7"); // printed separately
        iteratorMap.put(Trigger.class, Arrays.asList(t1, t2, t3, t4, t5, t6, t7).iterator());

        ReportEventProcessor processor = new ReportEventProcessor(iteratorMap);
        AllEventsCSVReportPrinter printer = new AllEventsCSVReportPrinter(processor, false,false);

        String expected = ";0;;;;;;;;;;;;;;;;;;;;;0;;t1\n" +
                        ";50;;;;;;;;;;;;;;;;;;;;;0;;t2\n" +
                        "0;100;100;1;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;0;;\n" +
                        "0;200;200;2;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;0;;t3\n" +
                        "0;300;300;3;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;0;;\n" +
                        ";500;;;;;;;;;;;;;;;;;;;;;0;;t4\n" +
                        "0;1000;1000;4;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;0;;t5\n" +
                        ";1050;;;;;;;;;;;;;;;;;;;;;0;;t6\n" +
                        ";1150;;;;;;;;;;;;;;;;;;;;;0;;t7\n";

        assertEquals(printToString(printer), expected);
    }

    @Test
    public void testAllEventsCSVReportPrinter_MoodStates() {
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

        MoodState t1 = new MoodState("0", State.NEUTRE.name(), 0); // printed separately
        MoodState t2 = new MoodState("0", State.NEUTRE.name(), 50); // merged into e1
        MoodState t3 = new MoodState("0", State.PLAISIR_INTENSE.name(), 150); // merged into e2
        MoodState t4 = new MoodState("0", State.NEUTRE.name(), 250); // printed separately
        MoodState t5 = new MoodState("0", State.FAIBLE_PLAISIR.name(), 290); // merged into e3 and e4
        MoodState t6 = new MoodState("0", State.PLAISIR_INTENSE.name(), 1050); // printed separately
        MoodState t7 = new MoodState("0", State.FAIBLE_PLAISIR.name(), 1150); // printed separately
        MoodState t8 = new MoodState("0", State.FAIBLE_PLAISIR.name(), 1150); // not printed
        iteratorMap.put(MoodState.class, Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8).iterator());

        ReportEventProcessor processor = new ReportEventProcessor(iteratorMap);
        AllEventsCSVReportPrinter printer = new AllEventsCSVReportPrinter(processor, false,false);

        String expected = ";0;;;;;;;;;;;;;;;;;;;;;1;;\n" +
                "0;100;100;1;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;1;;\n" +
                "0;200;200;2;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;3;;\n" +
                ";250;;;;;;;;;;;;;;;;;;;;;1;;\n" +
                "0;300;300;3;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;2;;\n" +
                "0;1000;1000;4;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;;;;;2;;\n" +
                ";1050;;;;;;;;;;;;;;;;;;;;;3;;\n" +
                ";1150;;;;;;;;;;;;;;;;;;;;;2;;\n";

        assertEquals(printToString(printer), expected);
    }

    private static String printToString(AllEventsCSVReportPrinter printer) {
        StringWriter w = new StringWriter();
        printer.print(w);
        return w.toString();
    }
}
