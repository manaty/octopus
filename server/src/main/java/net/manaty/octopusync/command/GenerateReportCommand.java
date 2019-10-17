package net.manaty.octopusync.command;

import io.bootique.cli.Cli;
import io.bootique.command.CommandOutcome;
import io.bootique.command.CommandWithMetadata;
import io.bootique.meta.application.CommandMetadata;
import io.bootique.meta.application.OptionMetadata;
import joptsimple.OptionSpec;
import net.manaty.octopusync.service.report.ReportService;

import javax.inject.Inject;
import javax.inject.Provider;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GenerateReportCommand extends CommandWithMetadata {

    private static final String HEADSET_ID_PARAM = "headset-id";
    private static final String FROM_PARAM = "from";
    private static final String TO_PARAM = "to";

    private Provider<ReportService> reportService;

    @Inject
    public GenerateReportCommand(Provider<ReportService> reportService) {
        super(CommandMetadata.builder(GenerateReportCommand.class)
                .addOption(OptionMetadata.builder(HEADSET_ID_PARAM, "Headset ID")
                        .valueRequired())
                .addOption(OptionMetadata.builder(FROM_PARAM,
                        "The beginning of report time interval in format [yyyy-MM-dd HH:mm:ss]")
                        .valueRequired())
                .addOption(OptionMetadata.builder(TO_PARAM,
                        "The end of report time interval in format [yyyy-MM-dd HH:mm:ss]")
                        .valueRequired()));

        this.reportService = reportService;
    }

    @Override
    public CommandOutcome run(Cli cli) {
        try {
            ReportService reportService = this.reportService.get();

            String headsetId = Objects.requireNonNull(cli.optionString(HEADSET_ID_PARAM), "Missing headset ID")
                    .trim();
            long fromMillisUtc = toUtcMillis(Objects.requireNonNull(cli.optionString(FROM_PARAM),
                    "Missing the beginning of report time interval").trim());
            long toMillisUtc = toUtcMillis(Objects.requireNonNull(cli.optionString(TO_PARAM),
                    "Missing the end of report time interval").trim());

            String reportPath = reportService.generate(headsetId, fromMillisUtc, toMillisUtc);
            System.err.println("Generated report: " + reportPath);

            return CommandOutcome.succeeded();
        } catch (Exception e) {
            return CommandOutcome.failed(1, e);
        }
    }

    private long toUtcMillis(String dateTime) {
        return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(dateTime.replace(' ', 'T')))
                .toInstant(ZoneOffset.from(ZonedDateTime.now())).toEpochMilli();
    }
}
