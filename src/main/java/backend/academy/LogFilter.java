package backend.academy;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LogFilter {
    private final ZonedDateTime from;
    private final ZonedDateTime to;
    private final String filterField;
    private String filterValue;

    private final Map<String, Predicate<NginxLog>> filterPredicates = Map.of(
        "address", log -> log.remoteAddress() != null && log.remoteAddress().contains(filterValue),
        "user", log -> log.remoteUser() != null && log.remoteUser().contains(filterValue),
        "method", log -> log.httpRequest().method() != null && log.httpRequest().method().equalsIgnoreCase(filterValue),
        "source", log -> log.httpRequest().uri() != null && log.httpRequest().uri().contains(filterValue),
        "status", log -> Integer.toString(log.status()).equals(filterValue),
        "referer", log -> log.httpReferer() != null && log.httpReferer().contains(filterValue),
        "agent", log -> log.httpUserAgent() != null && log.httpUserAgent().contains(filterValue)
    );

    public LogFilter(
        ZonedDateTime from,
        ZonedDateTime to,
        String filterField,
        String filterValue
    ) {
        this.from = from;
        this.to = to;
        this.filterField = filterField;
        this.filterValue = filterValue;
    }

    public Stream<NginxLog> filter(Stream<NginxLog> logs) {
        Predicate<NginxLog> fieldPredicate = filterField != null ? filterPredicates.get(filterField) : log -> true;

        return logs
            .filter(log -> from == null || log.localTime().isAfter(from))
            .filter(log -> to == null || log.localTime().isBefore(to))
            .filter(fieldPredicate);
    }
}
