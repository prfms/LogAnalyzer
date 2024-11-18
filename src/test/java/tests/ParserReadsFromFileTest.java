package tests;

import backend.academy.LogParser;
import backend.academy.StatisticsReport;
import backend.academy.StatisticsUpdater;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ParserReadsFromFileTest {
    @Test
    @Disabled("needs to be improved")
    public void ParserReadsFromFile() throws IOException {
        // Arrange
        Path tempDir = Paths.get("./src/test/resources");
        Files.createDirectories(tempDir);
        Path tempFile = tempDir.resolve("test_nginx_logs.txt");
        List<String> sampleLogs = List.of(
            "127.0.0.1 - - [10/Oct/2000:13:55:36 -0700] \"GET /apache_pb.gif HTTP/1.0\" 200 2326",
            "192.168.0.1 - - [11/Oct/2000:14:56:37 -0700] \"POST /login HTTP/1.1\" 401 789"
        );

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
            for (String log : sampleLogs) {
                writer.write(log);
                writer.newLine();
            }
        }

        StatisticsReport mockReport = Mockito.mock(StatisticsReport.class);
        StatisticsUpdater statisticsUpdater = new StatisticsUpdater(mockReport);
        LogParser parser = new LogParser(statisticsUpdater, null, null, null, null);

        // Act
        parser.parseFile(tempFile.toString());

        // Assert
        verify(mockReport, times(2)).incrementRequestNumber();
        Files.deleteIfExists(tempFile);
    }
}
