package net.manaty.octopusync.service.report;

import net.manaty.octopusync.api.State;
import net.manaty.octopusync.model.*;

import java.io.*;

public class AllEventsCSVReportPrinter {

    private static final String HEADER =
            "Timestamp event (global, en ms);Timestamp local server (global, en ms);Timestamp master server (global, en ms);Counter;" +
            "AF3;F7;F3;FC5;T7;P7;O1;O2;P8;T8;FC6;F4;F8;AF4;Q0;Q1;Q2;Q3;" +
            "Rp.;Musique;Tag";

    private static final char delimiter = ';';

    private final ReportEventProcessor processor;
    private final boolean shouldPrintHeader;
    private final boolean shouldNormalizeEegValues;

    public AllEventsCSVReportPrinter(
            ReportEventProcessor processor,
            boolean shouldPrintHeader,
            boolean shouldNormalizeEegValues) {
        this.processor = processor;
        this.shouldPrintHeader = shouldPrintHeader;
        this.shouldNormalizeEegValues = shouldNormalizeEegValues;
    }

    public void print(Writer w) {
        try (PrintWriter writer = new PrintWriter(w, false)) {
            if (shouldPrintHeader) {
                writer.println(HEADER);
            }
            processor.visitEvents(new PrintingVisitor(writer));
            writer.flush();
        }
    }

    public void print(File reportFile) {
        try {
            print(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportFile))));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    private class PrintingVisitor implements ReportEventProcessor.EventVisitor {

        private final PrintWriter writer;
        private int moodState;
        private UserMessage userMessage;
        private MotEvent motEvent;
        private Boolean musicOn;

        private long lastEegEventTimeLocal;
        private RunningMedian eegEventTimeMedian;

        private PrintingVisitor(PrintWriter writer) {
            this.writer = writer;
            this.moodState = State.NONE.getNumber();
            this.eegEventTimeMedian = new RunningMedian(1000);
        }

        @Override
        public void visit(Class<?> eventType, Timestamped event) {
            if (MoodState.class.equals(eventType)) {
                MoodState s = (MoodState) event;
                this.moodState = State.valueOf(s.getState()).getNumber();

            } else if (EegEvent.class.equals(eventType)) {
                EegEvent e = (EegEvent) event;
                // track time interval between current and last events
                if (lastEegEventTimeLocal != 0) {
                    eegEventTimeMedian.add(e.getTimeLocal() - lastEegEventTimeLocal);
                }
                // check if last user message should be printed in a separate record or merged into the current EEG record
                if (userMessage != null) {
                    long thresholdMillis = eegEventTimeMedian.median() * 2;
                    if ((e.getTimeLocal() - userMessage.time) > thresholdMillis) {
                        printUserMessage(userMessage.time, userMessage.message);
                        userMessage = null;
                    }
                }
                // timestamps
                writer.print(e.getTimeRelative());
                writer.print(delimiter);
                writer.print(e.getTimeLocal());
                writer.print(delimiter);
                writer.print(e.getTime());
                writer.print(delimiter);
                writer.print(e.getCounter());
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
                if (musicOn != null) {
                    writer.print((musicOn ? "1" : "0"));
                }
                writer.print(delimiter);
                if (userMessage != null) {
                    writer.print(userMessage.message);
                    userMessage = null;
                }

                writer.println();

                // track timestamp of the current event
                lastEegEventTimeLocal = e.getTimeLocal();

            } else if (Trigger.class.equals(eventType)) {
                Trigger trigger = (Trigger) event;
                if (trigger.getMessage().equals(Trigger.MESSAGE_MUSICON)) {
                    musicOn = true;
                } else if (trigger.getMessage().equals(Trigger.MESSAGE_MUSICOFF)) {
                    musicOn = false;
                } else if (lastEegEventTimeLocal != 0) { // triggers sent before the start of experience are skipped
                    if (userMessage != null) {
                        printUserMessage(userMessage.time, userMessage.message);
                        userMessage = null;
                    }
                    userMessage = new UserMessage(trigger.getHappenedTimeMillisUtc(), trigger.getMessage());
                }

            } else if (MotEvent.class.equals(eventType)) {
                motEvent = (MotEvent) event;
            } else {
                throw new IllegalStateException("Unknown event type: " + eventType.getName());
            }
        }

        private void printUserMessage(long happenedTimeMillisUtc, String message) {
            writer.print(delimiter);
            writer.print(happenedTimeMillisUtc);
            for (int i = 0; i < 21; i++) {
                writer.print(delimiter);
            }
            writer.print(moodState);
            writer.print(delimiter);
            if (musicOn != null) {
                writer.print((musicOn ? "1" : "0"));
            }
            writer.print(delimiter);
            writer.print(message);
            writer.println();
        }
    }

    private static class UserMessage {
        final long time;
        final String message;

        private UserMessage(long time, String message) {
            this.time = time;
            this.message = message;
        }
    }
}
