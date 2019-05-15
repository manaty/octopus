package net.manaty.octopusync.service.report;

import net.manaty.octopusync.di.ReportRoot;
import net.manaty.octopusync.service.db.Storage;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Path;
import java.util.Map;

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
        return null;
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
