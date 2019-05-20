package net.manaty.octopusync.service.report;

import net.manaty.octopusync.di.ReportRoot;
import net.manaty.octopusync.model.EegEvent;
import net.manaty.octopusync.model.MoodState;
import net.manaty.octopusync.model.Timestamped;
import net.manaty.octopusync.service.db.Storage;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter REPORT_NAME_DATETIME_FORMATTER;

    static {
        REPORT_NAME_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    }

    private final Storage storage;
    private final Path reportRoot;

    @Inject
    public ReportServiceImpl(Storage storage, @ReportRoot Path reportRoot) {
        this.storage = storage;
        this.reportRoot = reportRoot;
    }

    @Override
    public Map<String, String> generate(long fromMillisUtc, long toMillisUtc) {
        return storage.getHeadsetIdsFromEegEvents().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        headsetId -> generate(headsetId, fromMillisUtc, toMillisUtc)));
    }

    @Override
    public String generate(String headsetId, long fromMillisUtc, long toMillisUtc) {
        Path relativePath = Paths.get(getReportName(headsetId, fromMillisUtc, toMillisUtc));
        generate(relativePath,
                storage.getMoodStates(headsetId, fromMillisUtc, toMillisUtc),
                storage.getEegEvents(headsetId, fromMillisUtc, toMillisUtc));
        return relativePath.toString();
    }

    private static String getReportName(String headsetId, long fromMillisUtc, long toMillisUtc) {
        String from = getReportNameDateTimeString(fromMillisUtc);
        String to = getReportNameDateTimeString(toMillisUtc);
        return headsetId + "-" + from + "-" + to + ".csv";
    }

    private static String getReportNameDateTimeString(long epochMillis) {
        return REPORT_NAME_DATETIME_FORMATTER.format(Instant.ofEpochMilli(epochMillis)
                .atZone(ZoneId.systemDefault()));
    }

    private void generate(
            Path relativePath,
            Stream<MoodState> clientStates,
            Stream<EegEvent> eegEvents) {

        Map<Class<?>, Iterator<? extends Timestamped>> m = new HashMap<>();
        m.put(MoodState.class, clientStates.iterator());
        m.put(EegEvent.class, eegEvents.iterator());

        AllEventsCSVReportPrinter printer = new AllEventsCSVReportPrinter(new ReportEventProcessor(m));

        File file = reportRoot.resolve(relativePath).toFile();
        try {
            if (file.exists()) {
                if (!file.delete()) {
                    throw new IllegalStateException("Failed to delete existing file: " + file.getAbsolutePath());
                }
            }
            if (!file.createNewFile()) {
                throw new IllegalStateException("Failed to create file: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        printer.print(file);
    }

    @Override
    public InputStream get(String path) {
        Path fullPath = reportRoot.resolve(path);
        File reportFile = fullPath.toFile();
        if (!reportFile.exists()) {
            throw new IllegalStateException("No such report: " + fullPath);
        }
        try {
            return new FileInputStream(reportFile);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException("Missing file: " + fullPath, e);
        }
    }
}
