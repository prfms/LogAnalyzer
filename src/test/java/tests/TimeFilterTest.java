package tests;

import backend.academy.logSource.FileLogSource;
import backend.academy.LogFilter;
import backend.academy.LogProcessor;
import backend.academy.NginxLogParser;
import backend.academy.StatisticsReport;
import backend.academy.StatisticsUpdater;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimeFilterTest {
    @Test
    void testFilterLogsByTimeRange() {
        // Arrange
        ZonedDateTime from = ZonedDateTime.parse("2024-12-12T06:00:00+00:00");
        ZonedDateTime to = ZonedDateTime.parse("2024-12-12T07:00:00+00:00");
        StatisticsReport mockReport = mock(StatisticsReport.class);
        StatisticsUpdater updater = new StatisticsUpdater(mockReport);
        LogFilter filter = new LogFilter(from, to, null, null);
        NginxLogParser parser = new NginxLogParser();
        FileLogSource mockLogSource = Mockito.mock(FileLogSource.class);
        LogProcessor logProcessor = new LogProcessor(mockLogSource, parser, filter, updater);

        String log1 = "127.0.0.1 - - [12/Dec/2024:06:25:24 +0000] \"GET /index.html HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\"";
        String log2 = "127.0.0.1 - - [12/Dec/2024:07:30:24 +0000] \"GET /about.html HTTP/1.1\" 200 2345 \"-\" \"Mozilla/5.0\"";

        // Act
        when(mockLogSource.getLogs()).thenReturn(Stream.of(log1, log2));
        logProcessor.process();

        // Assert
        verify(mockReport, times(1)).addSource("/index.html");
    }
}
