package tests;

import backend.academy.StatisticsReport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatisticsReportTest {
    @Test
    @Disabled("needs to be improved")
    public void testWriteAdocFile(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path tempFile = tempDir.resolve("report.adoc");

        StatisticsReport report = new StatisticsReport();
        report.requestNumber(10L);

        report.mostFrequentSources(List.of(
            Map.entry("127.0.0.1", 5),
            Map.entry("192.168.0.1", 3)
        ));

        report.mostFrequentAnswerCode(List.of(
            Map.entry(200, 7),
            Map.entry(404, 2)
        ));

        report.mostFrequentIp(List.of(
            Map.entry("127.0.0.1", 6),
            Map.entry("192.168.0.1", 4)
        ));

        report.mostFrequentHttpMethod(List.of(
            Map.entry("GET", 8),
            Map.entry("POST", 4)
        ));

        report.answerSize(List.of(500, 600, 700, 800));

        report.totalAnswerSize(2600L);
        report.requestNumber(4L);

        report.outputPath(tempFile.toString());

        // Act
        report.writeAdocFile();

        // Assert
        List<String> lines = Files.readAllLines(tempFile);
        String content = String.join("\n", lines);

        assertTrue(content.contains("=== General Information ==="));
        assertTrue(content.contains("| File(-s)                  |"));
        assertTrue(content.contains("| Number of requests        | 10"));

        assertTrue(content.contains("=== Frequency of Client Requests ==="));
        assertTrue(content.contains("| Source                      | Frequency"));
        assertTrue(content.contains("| 127.0.0.1                   | 5"));
        assertTrue(content.contains("| 192.168.0.1                 | 3"));

        assertTrue(content.contains("=== Answer Codes ==="));
        assertTrue(content.contains("| Code | Meaning                | Frequency"));
        assertTrue(content.contains("| 200  | OK                     | 7"));
        assertTrue(content.contains("| 404  | Not Found              | 2"));

        assertTrue(content.contains("=== Most Frequent IP Addresses ==="));
        assertTrue(content.contains("| IP Address               | Frequency"));
        assertTrue(content.contains("| 127.0.0.1                | 6"));
        assertTrue(content.contains("| 192.168.0.1              | 4"));
    }
}
