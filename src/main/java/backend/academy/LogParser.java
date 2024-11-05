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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LogParser {
    private static final String LOG_PATTERN =
        "^(\\S+) - (\\S*) \\[(.*?)\\] \"(.*?)\" (\\d{3}) (\\d+) \"(.*?)\" \"(.*?)\"$";
    private static final Pattern pattern = Pattern.compile(LOG_PATTERN);
    private final StatisticsUpdater statisticsUpdater;

    public LogParser(StatisticsUpdater updater) {
        this.statisticsUpdater = updater;
    }

    public NginxLog parse(String singleLog) {
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

            nginxLog.request(matcher.group(4));
            nginxLog.status(Integer.parseInt(matcher.group(5)));
            nginxLog.bytesSent(Integer.parseInt(matcher.group(6)));
            nginxLog.httpReferer(matcher.group(7).equals("-") ? null : matcher.group(7));
            nginxLog.httpUserAgent(matcher.group(8));

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

    private void parseStrings(Stream<String> source) {
        source.map(this::parse).forEach(statisticsUpdater::updateStatistics);
    }
}
