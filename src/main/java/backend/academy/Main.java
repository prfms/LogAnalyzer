package backend.academy;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import lombok.extern.log4j.Log4j2;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Main {
    @Parameter(names = "--path", description = "Path to one or more NGINX log files", required = true)
    private static String path = null;

    @Parameter(names = "--from", description = "Start date in ISO8601 format")
    private static String fromStr = null;

    private ZonedDateTime from = null;

    @Parameter(names = "--to", description = "End date in ISO8601 format")
    private static String toStr = null;

    private ZonedDateTime to = null;

    @Parameter(names = "--format", description = "Output format: markdown or adoc")
    private static String format = "markdown";

    @Parameter(names = "--filter-field",
        description = "Field of nginx log to apply filter to. Options: address, user, method, source, status, referer, agent")
    private static String filterField = null;

    @Parameter(names = "--filter-value", description = "Value that must be in filter field")
    private static String filterValue = null;

    @Parameter(names = "--help", help = true)
    private boolean help;

    private final String globalPath = ".";

    public static void main(String[] args) throws IOException, URISyntaxException {
// .\src\main\resources\logs_samples.txt

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
            log.error("Parameter exception!: {}", e.getMessage());
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
            return;
        }

        if (path == null || path.trim().isEmpty()) {
            log.error("Path is empty!");
            return;
        }

        if (!format.equals("markdown") && !format.equals("adoc")) {
            log.error("Invalid format: {}", format);
            return;
        }
        List<String> paths = new ArrayList<>();
        paths.add(path); // вынести парсинг glob в другой класс
        StatisticsReport report = new StatisticsReport(paths, from.toString(), to.toString());
        StatisticsUpdater calculator = new StatisticsUpdater(report);
        LogParser parser = new LogParser(calculator, from, to, filterField, filterValue);


        try {
            if (path.startsWith("http")) {
                parser.parseURL(path);
            } else {
                parser.parseGlob(path, globalPath);
            }
        } catch (Exception e) {
            log.error("Error while parsing file/URL: {}", path, e);
            return;
        }

        if (format.equals("adoc")) {
            report.writeAdocFile();
        } else {
            report.writeMarkdownFile();
        }

        System.out.println("Количество запросов: " + report.requestNumber());
        System.out.println("Средний размер ответа: " + report.getAverageAnswerSize());
        System.out.println("Самые частые источники запросов: " + report.getMostFrequentSources());
        System.out.println("Самые частые коды ответа: " + report.getMostFrequentAnswerCode());
        System.out.println("95% размера ответа сервера: " + report.percentileSizeAnswer(95));

    }
}

