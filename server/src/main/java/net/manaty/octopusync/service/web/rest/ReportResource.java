package net.manaty.octopusync.service.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.manaty.octopusync.service.report.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Path("report")
public class ReportResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportResource.class);

    private final ReportService reportService;
    private final ObjectMapper mapper;

    @Inject
    public ReportResource(ReportService reportService) {
        this.reportService = reportService;
        this.mapper = new ObjectMapper();
    }

    @GET
    @Path("generate")
    @Produces(MediaType.APPLICATION_JSON)
    public String generate(
            @QueryParam("headset_id") String headsetId,
            @QueryParam("from") String from,
            @QueryParam("to") String to) {

        long fromMillis, toMillis;
        if (from == null) {
            fromMillis = getEpochMillis(LocalDate.now().atStartOfDay());
        } else {
            fromMillis = getEpochMillis(getTodayWithTime(parseLocalTime(from)));
        }
        if (to == null) {
            toMillis = getEpochMillis(LocalDate.now().plusDays(1).atStartOfDay());
        } else {
            toMillis = getEpochMillis(getTodayWithTime(parseLocalTime(to)));
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Will build report with params: headset ({}), from ({}), to ({})",
                    (headsetId == null? "all" : headsetId), Instant.ofEpochMilli(fromMillis), Instant.ofEpochMilli(toMillis));
        }

        Map<String, String> result = new HashMap<>();
        if (headsetId == null) {
            reportService.generate(fromMillis, toMillis)
                    .forEach(result::put);
        } else {
            String path = reportService.generate(headsetId, fromMillis, toMillis);
            result.put(headsetId, path);
        }

        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalTime parseLocalTime(String s) {
        return LocalTime.from(DateTimeFormatter.ISO_LOCAL_TIME.parse(s));
    }

    private LocalDateTime getTodayWithTime(LocalTime time) {
        return LocalTime.from(time).atDate(LocalDate.now());
    }

    private long getEpochMillis(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @GET
    @Path("get/{path}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response get(@PathParam("path") String path) {
        if (path == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        return Response.ok(reportService.get(path)).build();
    }
}
