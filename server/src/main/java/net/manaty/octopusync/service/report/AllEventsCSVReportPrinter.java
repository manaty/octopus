package net.manaty.octopusync.service.report;

import net.manaty.octopusync.api.State;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.Timestamped;
import net.manaty.octopusync.model.Trigger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;

public class AllEventsCSVReportPrinter {

    private static final String HEADER =
            "Timestamp (aprËs lancement, en s);Timestamp (global, en ms);" +
            "AF3;F7;F3;FC5;T7;P7;O1;O2;P8;T8;FC6;F4;F8;AF4;" +
            "Rp.;Musique;Tag";

    private static final char delimiter = ',';

    private final ReportEventProcessor processor;

    public AllEventsCSVReportPrinter(ReportEventProcessor processor) {
        this.processor = processor;
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

    private static class PrintingVisitor implements ReportEventProcessor.EventVisitor {

        private final PrintWriter writer;
        private int moodState;
        private String triggerMessage;

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
