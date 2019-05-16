package net.manaty.octopusync.service.report;

import net.manaty.octopusync.di.ReportRoot;
import net.manaty.octopusync.model.*;
import net.manaty.octopusync.service.db.Storage;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class ReportServiceImpl implements ReportService {

    private final Storage storage;
    private final Path reportRoot;

    @Inject
    public ReportServiceImpl(Storage storage, @ReportRoot Path reportRoot) {
        this.storage = storage;
        this.reportRoot = reportRoot;
    }

    @Override
    public Map<String, String> generate(long fromMillisUtc, long toMillisUtc) {
        return null;
    }

    @Override
    public String generate(String headsetId, long fromMillisUtc, long toMillisUtc) {
        return generate(
                storage.getS2SSyncResults(fromMillisUtc, toMillisUtc),
                storage.getClientSyncResults(fromMillisUtc, toMillisUtc),
                storage.getMoodStates(fromMillisUtc, toMillisUtc),
                storage.getEegEvents(fromMillisUtc, toMillisUtc)
        );
    }

    private String generate(
            Stream<S2STimeSyncResult> s2sTimeSyncResults,
            Stream<ClientTimeSyncResult> clientTimeSyncResults,
            Stream<MoodState> clientStates,
            Stream<EegEvent> eegEvents) {

        Map<Class<?>, Iterator<? extends Timestamped>> m = new HashMap<>();
        m.put(S2STimeSyncResult.class, s2sTimeSyncResults.iterator());
        m.put(ClientTimeSyncResult.class, clientTimeSyncResults.iterator());
        m.put(MoodState.class, clientStates.iterator());
        m.put(EegEvent.class, eegEvents.iterator());

        AllEventsCSVReportPrinter printer = new AllEventsCSVReportPrinter(new ReportEventProcessor(m));

        String relativePath = "test.csv";
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

        return relativePath;
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
