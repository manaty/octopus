package net.manaty.octopusync.service.report;

import net.manaty.octopusync.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class AllEventsCSVReportPrinter {

    private static final char delimiter = ',';

    private final ReportEventProcessor processor;

    public AllEventsCSVReportPrinter(ReportEventProcessor processor) {
        this.processor = processor;
    }

    public void print(File reportFile) {
        try (PrintWriter writer = new PrintWriter(reportFile)) {
            processor.visitEvents(new PrintingVisitor(writer));
            writer.flush();
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class PrintingVisitor implements ReportEventProcessor.EventVisitor {

        private final PrintWriter writer;

        private long serverDelay;
        private Map<String, Long> clientDelaysByHeadsetId;

        private PrintingVisitor(PrintWriter writer) {
            this.writer = writer;
            this.clientDelaysByHeadsetId = new HashMap<>();
        }

        @Override
        public void visit(Class<?> eventType, Timestamped event) {
            if (S2STimeSyncResult.class.equals(eventType)) {
                S2STimeSyncResult r = (S2STimeSyncResult) event;
                System.err.println(r);
                if (r.getError() == null) {
                    serverDelay = r.getDelay();
                }

            } else if (ClientTimeSyncResult.class.equals(eventType)) {
                ClientTimeSyncResult r = (ClientTimeSyncResult) event;
                System.err.println(r);
                if (r.getError() == null) {
                    clientDelaysByHeadsetId.put(r.getHeadsetId(), r.getDelay());
                }

            } else if (MoodState.class.equals(eventType)) {
                MoodState s = (MoodState) event;
                System.err.println(s);
                long delay = clientDelaysByHeadsetId.getOrDefault(s.getHeadsetId(), 0L);
                writer.print(s.getSinceTimeUtc() - delay - serverDelay);
                writer.print(delimiter);
                writer.print(s.getState());
                writer.println();

            } else if (EegEvent.class.equals(eventType)) {
                EegEvent e = (EegEvent) event;
                System.err.println(e);
                writer.print(e.getTime() - serverDelay);
                writer.print(delimiter);
                writer.print(e.getSignalQuality());
                writer.print(delimiter);
                writer.print(e.getF3());
                writer.println();

            } else {
                throw new IllegalStateException("Unknown event type: " + eventType.getName());
            }
        }
    }
}
