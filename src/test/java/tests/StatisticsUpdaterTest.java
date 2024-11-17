package tests;

import backend.academy.NginxLog;
import backend.academy.StatisticsReport;
import backend.academy.StatisticsUpdater;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class StatisticsUpdaterTest {

    private StatisticsReport mockReport;
    private StatisticsUpdater statisticsUpdater;

    @BeforeEach
    void setUp() {
        mockReport = Mockito.mock(StatisticsReport.class);
        statisticsUpdater = new StatisticsUpdater(mockReport);
    }

    @Test
    void updateStatistics_ShouldCallIncrementRequestNumber() {
        // Arrange
        NginxLog nginxLog = createTestLog();

        // Act
        statisticsUpdater.updateStatistics(nginxLog);

        // Assert
        verify(mockReport, times(1)).incrementRequestNumber();
    }

    @Test
    void updateStatistics_ShouldAddSource() {
        // Arrange
        NginxLog nginxLog = createTestLog();

        // Act
        statisticsUpdater.updateStatistics(nginxLog);

        // Assert
        verify(mockReport, times(1)).addSource("/index.html");
    }

    @Test
    void updateStatistics_ShouldAddAnswerCode() {
        // Arrange
        NginxLog nginxLog = createTestLog();

        // Act
        statisticsUpdater.updateStatistics(nginxLog);

        // Assert
        verify(mockReport, times(1)).addAnswerCode(200);
    }

    @Test
    void updateStatistics_ShouldAddAnswerSize() {
        // Arrange
        NginxLog nginxLog = createTestLog();

        // Act
        statisticsUpdater.updateStatistics(nginxLog);

        // Assert
        verify(mockReport, times(1)).addAnswerSize(1234);
    }

    @Test
    void updateStatistics_ShouldAddIp() {
        // Arrange
        NginxLog nginxLog = createTestLog();

        // Act
        statisticsUpdater.updateStatistics(nginxLog);

        // Assert
        verify(mockReport, times(1)).addIp("127.0.0.1");
    }

    @Test
    void updateStatistics_ShouldAddHttpMethod() {
        // Arrange
        NginxLog nginxLog = createTestLog();

        // Act
        statisticsUpdater.updateStatistics(nginxLog);

        // Assert
        verify(mockReport, times(1)).addHttpMethod("GET");
    }

    private NginxLog createTestLog() {
        NginxLog log = new NginxLog();
        log.remoteAddress("127.0.0.1");
        log.httpRequest(new NginxLog.HttpRequest("GET", "/index.html", "HTTP/1.1"));
        log.status(200);
        log.bytesSent(1234);
        return log;
    }
}
