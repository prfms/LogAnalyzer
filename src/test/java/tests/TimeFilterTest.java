package tests;

import backend.academy.LogParser;
import backend.academy.StatisticsReport;
import backend.academy.StatisticsUpdater;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TimeFilterTest {
    @Test
    void testFilterLogsByTimeRange() {
        // Arrange
        ZonedDateTime from = ZonedDateTime.parse("2024-12-12T06:00:00+00:00");
        ZonedDateTime to = ZonedDateTime.parse("2024-12-12T07:00:00+00:00");
        StatisticsReport mockReport = mock(StatisticsReport.class);
        StatisticsUpdater statisticsUpdater = new StatisticsUpdater(mockReport);

        LogParser parser = new LogParser(statisticsUpdater, from, to, null, null);
        String log1 = "127.0.0.1 - - [12/Dec/2024:06:25:24 +0000] \"GET /index.html HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\"";
        String log2 = "127.0.0.1 - - [12/Dec/2024:07:30:24 +0000] \"GET /about.html HTTP/1.1\" 200 2345 \"-\" \"Mozilla/5.0\"";

        // Act
        parser.parseStrings(Stream.of(log1, log2));

        // Assert
        verify(mockReport, times(1)).addSource("/index.html");
    }
}
