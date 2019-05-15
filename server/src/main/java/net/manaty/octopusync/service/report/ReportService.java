package net.manaty.octopusync.service.report;

import java.io.InputStream;
import java.util.Map;

public interface ReportService {

    /**
     * @return Map of reports for all headsets; key is headset ID, value is path to report
     */
    Map<String, String> generate(long fromMillisUtc, long toMillisUtc);

    /**
     * @return Path to report for the specified headset ID
     */
    String generate(String headsetId, long fromMillisUtc, long toMillisUtc);

    InputStream get(String path);
}
