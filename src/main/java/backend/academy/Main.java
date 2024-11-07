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
    @Parameter(names = "--path", description = "Path to one or more NGINX log files")
    private List<String> paths = new ArrayList<>();

    @Parameter(names = "--from", description = "Start date in ISO8601 format")
    private String fromStr = null;
    private ZonedDateTime from = null;

    @Parameter(names = "--to", description = "End date in ISO8601 format")
    private String toStr = null;
    private ZonedDateTime to = null;

    @Parameter(names = "--format", description = "Output format: markdown or adoc")
    private String format = "markdown"; // значение по умолчанию

    @Parameter(names = "--help", help = true)
    private boolean help;

    public static void main(String[] args) throws IOException, URISyntaxException {
// .\src\main\resources\logs_samples.txt
        for (String arg : args) {
            System.out.println(arg);
        }

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

        if (!format.equals("markdown") && !format.equals("adoc")) {
            log.error("Invalid format: {}", format);
            return;
        }

        StatisticsReport report = new StatisticsReport();
        StatisticsUpdater calculator = new StatisticsUpdater(report);
        LogParser parser = new LogParser(calculator, from, to);

        for (String path : paths) {
            try {
                if (path.startsWith("http")) {
                    parser.parseURL(path);
                } else {
                    parser.parseFile(path);
                }
            } catch (Exception e) {
                log.error("Error while parsing file/URL: {}", path, e);
            }
        }
        System.out.println("Количество запросов: " + report.requestNumber());
        System.out.println("Средний размер ответа: " + report.getAverageAnswerSize());
        System.out.println("Самые частые источники запросов: " + report.getMostFrequentSources());
        System.out.println("Самые частые коды ответа: " + report.getMostFrequentAnswerCode());
        System.out.println("95% размера ответа сервера: " + report.percentileSizeAnswer(95));

        if (format.equals("markdown")) {
            report.writeMarkdownFile();
        } else {
            report.writeAdocFile();
        }
    }
}

