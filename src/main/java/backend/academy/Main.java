package backend.academy;

import backend.academy.logSource.FileLogSource;
import backend.academy.logSource.LogSource;
import backend.academy.logSource.UrlLogSource;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {

    private static final String MARKDOWN = "markdown";
    private static final String ADOC = "adoc";

    @Parameter(names = "--path", description = "Path to one or more NGINX log files", required = true)
    private static String path = null;

    @Parameter(names = "--from", description = "Start date in ISO8601 format")
    private static String fromStr = null;

    private ZonedDateTime from = null;

    @Parameter(names = "--to", description = "End date in ISO8601 format")
    private static String toStr = null;

    private ZonedDateTime to = null;

    @Parameter(names = "--format", description = "Output format: markdown or adoc")
    private static String format = MARKDOWN;

    @Parameter(names = "--filter-field",
        description = "Field of nginx log to apply filter to. "
            + "Options: address, user, method, source, status, referer, agent")
    private static String filterField = null;

    @Parameter(names = "--filter-value", description = "Value that must be in filter field")
    private static String filterValue = null;

    @Parameter(names = "--help", help = true)
    private boolean help;

    public static void main(String[] args) throws IOException, URISyntaxException {
        Main main = new Main();
        JCommander jCommander = JCommander.newBuilder()
            .addObject(main)
            .build();

        try {
            jCommander.parse(args);
            if (main.help) {
                jCommander.usage();
                return;
            }
            main.run();
        } catch (Exception e) {
            log.error("Parameter exception: {}", e.getMessage());
            jCommander.usage();
        }
    }

    public void run() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        log.info("Parsed arguments: path={}, from={}, to={}, format={}, filterField={}, filterValue={}",
            path, fromStr, toStr, format, filterField, filterValue);

        try {
            if (fromStr != null) {
                from = ZonedDateTime.parse(fromStr, formatter);
            }
            if (toStr != null) {
                to = ZonedDateTime.parse(toStr, formatter);
            }
        } catch (DateTimeParseException e) {
            log.error("Date parsing error: {}", e.getMessage());
            throw new LogParserException("Failed to parse dates", e);
        }

        if (path == null || path.trim().isEmpty()) {
            log.error("Path is empty!");
            throw new LogParserException("Path cannot be null or empty");
        }

        if ((!MARKDOWN.equals(format) && !ADOC.equals(format))) {
            log.error("Invalid format: {}", format);
            throw new LogParserException("Unsupported format: " + format);
        }

        LogSource logSource;
        if (path.startsWith("http")) {
            logSource = new UrlLogSource(path);
        } else {
            logSource = new FileLogSource(path);
        }

        StatisticsReport report = new StatisticsReport(from, to);
        StatisticsUpdater updater = new StatisticsUpdater(report);
        LogFilter filter = new LogFilter(from, to, filterField, filterValue);
        NginxLogParser parser = new NginxLogParser();

        LogProcessor logProcessor = new LogProcessor(logSource, parser, filter, updater);

        logProcessor.process();

        if (ADOC.equals(format)) {
            report.writeAdocFile();
        } else {
            report.writeMarkdownFile();
        }
    }
}

