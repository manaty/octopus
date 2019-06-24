package net.manaty.octopusync.service.report;

import net.manaty.octopusync.api.State;
import net.manaty.octopusync.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public class AllEventsCSVReportPrinter {

    private static final String HEADER =
            "Timestamp (aprËs lancement, en s);Timestamp (global, en ms);" +
            "AF3;F7;F3;FC5;T7;P7;O1;O2;P8;T8;FC6;F4;F8;AF4;Q0;Q1;Q2;Q3;" +
            "Rp.;Musique;Tag";

    private static final char delimiter = ';';

    private final ReportEventProcessor processor;
    private final boolean shouldNormalizeEegValues;

    public AllEventsCSVReportPrinter(ReportEventProcessor processor, boolean shouldNormalizeEegValues) {
        this.processor = processor;
        this.shouldNormalizeEegValues = shouldNormalizeEegValues;
    }

    public void print(File reportFile) {
        try (PrintWriter writer = new PrintWriter(reportFile)) {
            writer.println(HEADER);
            processor.visitEvents(new PrintingVisitor(writer));
            writer.flush();
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    private class PrintingVisitor implements ReportEventProcessor.EventVisitor {

        private final PrintWriter writer;
        private int moodState;
        private String triggerMessage;
        private MotEvent motEvent;

        private PrintingVisitor(PrintWriter writer) {
            this.writer = writer;
            this.moodState = State.NEUTRE.getNumber();
        }

        @Override
        public void visit(Class<?> eventType, Timestamped event) {
            if (MoodState.class.equals(eventType)) {
                MoodState s = (MoodState) event;
                this.moodState = State.valueOf(s.getState()).getNumber();

            } else if (EegEvent.class.equals(eventType)) {
                EegEvent e = (EegEvent) event;
                // timestamps
                writer.print(0);
                writer.print(delimiter);
                writer.print(e.getTime());
                writer.print(delimiter);
                // values
                if (shouldNormalizeEegValues) {
                    writer.print(e.getAf3() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getF7() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getF3() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getFc5() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getT7() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getP7() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getO1() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getO2() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getP8() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getT8() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getFc6() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getF4() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getF8() - 4000.0);
                    writer.print(delimiter);
                    writer.print(e.getAf4() - 4000.0);
                    writer.print(delimiter);
                } else {
                    writer.print(e.getAf3());
                    writer.print(delimiter);
                    writer.print(e.getF7());
                    writer.print(delimiter);
                    writer.print(e.getF3());
                    writer.print(delimiter);
                    writer.print(e.getFc5());
                    writer.print(delimiter);
                    writer.print(e.getT7());
                    writer.print(delimiter);
                    writer.print(e.getP7());
                    writer.print(delimiter);
                    writer.print(e.getO1());
                    writer.print(delimiter);
                    writer.print(e.getO2());
                    writer.print(delimiter);
                    writer.print(e.getP8());
                    writer.print(delimiter);
                    writer.print(e.getT8());
                    writer.print(delimiter);
                    writer.print(e.getFc6());
                    writer.print(delimiter);
                    writer.print(e.getF4());
                    writer.print(delimiter);
                    writer.print(e.getF8());
                    writer.print(delimiter);
                    writer.print(e.getAf4());
                    writer.print(delimiter);
                }
                // gyroscopic data
                if (motEvent != null) {
                    writer.print(motEvent.getQ0());
                    writer.print(delimiter);
                    writer.print(motEvent.getQ1());
                    writer.print(delimiter);
                    writer.print(motEvent.getQ2());
                    writer.print(delimiter);
                    writer.print(motEvent.getQ3());
                    writer.print(delimiter);
                    motEvent = null;
                } else {
                    writer.print(delimiter);
                    writer.print(delimiter);
                    writer.print(delimiter);
                    writer.print(delimiter);
                }
                // misc.
                writer.print(moodState);
                writer.print(delimiter);
                //writer.print(Musique);
                writer.print(delimiter);
                if (triggerMessage != null) {
                    writer.print(triggerMessage);
                    triggerMessage = null;
                }

                writer.println();

            } else if (Trigger.class.equals(eventType)) {
                Trigger trigger = (Trigger) event;
                triggerMessage = trigger.getMessage();

            } else {
                throw new IllegalStateException("Unknown event type: " + eventType.getName());
            }
        }
    }
}
