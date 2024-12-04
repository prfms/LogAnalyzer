package tests;

import backend.academy.logSource.FileLogSource;
import backend.academy.LogFilter;
import backend.academy.LogProcessor;
import backend.academy.logSource.LogSource;
import backend.academy.NginxLogParser;
import backend.academy.StatisticsReport;
import backend.academy.StatisticsUpdater;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ParserReadsFromFileTest {
    @Test
    public void ParserReadsFromFile(){

        // Arrange
        LogSource logSource = new FileLogSource("./src/test/resources/test_nginx_logs.txt");

        StatisticsReport mockReport = Mockito.mock(StatisticsReport.class);
        StatisticsUpdater updater = new StatisticsUpdater(mockReport);
        LogFilter filter = new LogFilter(null, null, null, null);
        NginxLogParser parser = new NginxLogParser();

        LogProcessor logProcessor = new LogProcessor(logSource, parser, filter, updater);

        // Act
        logProcessor.process();

        // Assert
        verify(mockReport, times(2)).incrementRequestNumber();
    }
}
