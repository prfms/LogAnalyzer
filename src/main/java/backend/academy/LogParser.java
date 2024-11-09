package backend.academy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LogParser {
    private static final String LOG_PATTERN =
        "^(\\S+) - (\\S*) \\[(.*?)\\] \"(.*?) (.*?)\" (\\d{3}) (\\d+) \"(.*?)\" \"(.*?)\"$";
    private static final Pattern pattern = Pattern.compile(LOG_PATTERN);
    private final StatisticsUpdater statisticsUpdater;
    private final ZonedDateTime from;
    private final ZonedDateTime to;
    private final String filterField;
    private String filterValue;

    public LogParser(StatisticsUpdater updater, ZonedDateTime from, ZonedDateTime to, String filterField, String filterValue) {
        this.statisticsUpdater = updater;
        this.from = from;
        this.to = to;
        this.filterField = filterField;
        this.filterValue = filterValue;
    }

    private final Map<String, Predicate<NginxLog>> filterPredicates = Map.of(
        "address", log -> log.remoteAddress() != null && log.remoteAddress().contains(filterValue),
        "user", log -> log.remoteUser() != null && log.remoteUser().contains(filterValue),
        "method", log -> log.requestMethod() != null && log.requestMethod().equalsIgnoreCase(filterValue),
        "source", log -> log.requestSource() != null && log.requestSource().contains(filterValue),
        "status", log -> Integer.toString(log.status()).equals(filterValue),
        "referer", log -> log.httpReferer() != null && log.httpReferer().contains(filterValue),
        "agent", log -> log.httpUserAgent() != null && log.httpUserAgent().contains(filterValue)
    );

    private NginxLog parse(String singleLog) {
        Matcher matcher = pattern.matcher(singleLog);

        if (matcher.find()) {
            NginxLog nginxLog = new NginxLog();
            nginxLog.remoteAddress(matcher.group(1));
            nginxLog.remoteUser(matcher.group(2).isEmpty() || matcher.group(2).equals("-") ? null : matcher.group(2));

            String timeString = matcher.group(3);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            try {
                nginxLog.localTime(ZonedDateTime.parse(timeString, formatter));
            } catch (DateTimeParseException e) {
                log.error("Incorrect time format {}", timeString, e);
            }

            nginxLog.requestMethod(matcher.group(4));
            nginxLog.requestSource(matcher.group(5));
            nginxLog.status(Integer.parseInt(matcher.group(6)));
            nginxLog.bytesSent(Integer.parseInt(matcher.group(7)));
            nginxLog.httpReferer(matcher.group(8).equals("-") ? null : matcher.group(8));
            nginxLog.httpUserAgent(matcher.group(9));

            return nginxLog;
        } else {
            throw new IllegalArgumentException("Строка не соответствует формату лога Nginx.");
        }
    }

    public void parseFile(String fileName) {
        Path filePath = Paths.get(fileName);
        try {
            parseStrings(Files.lines(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void parseURL(String stringUrl) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(stringUrl))
                .GET()
                .build();

            HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Request was interrupted", e);
            }

            try (BufferedReader reader = new BufferedReader(new StringReader(response.body()))) {
                parseStrings(reader.lines());
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<NginxLog> filterLogs(Stream<NginxLog> stream) {
        Stream<NginxLog> filteredStream = Stream.empty();

        if (from != null && to != null) {
             filteredStream = stream.filter(log -> log.localTime().isAfter(from) && log.localTime().isBefore(to));
        }

        if (filterField != null) {
            Predicate<NginxLog> fieldPredicate = filterPredicates.get(filterField);
            if (fieldPredicate != null) {
                filteredStream = filteredStream.filter(fieldPredicate);
            }
        }

        return filteredStream;
    }

    private void parseStrings(Stream<String> source) {
        filterLogs(source.map(this::parse)).forEach(statisticsUpdater::updateStatistics);
    }
}
