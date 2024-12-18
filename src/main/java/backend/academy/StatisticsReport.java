package backend.academy;

import com.google.common.math.Quantiles;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter @Setter
@Log4j2
public class StatisticsReport {
    private List<String> fileNames = new ArrayList<>();
    private String from;
    private String to;
    private long requestNumber;
    private List<Map.Entry<String, Integer>> mostFrequentSources;
    private List<Map.Entry<Integer, Integer>> mostFrequentAnswerCode;
    private List<Map.Entry<String, Integer>> mostFrequentIp;
    private List<Map.Entry<String, Integer>> mostFrequentHttpMethod;
    private List<Integer> answerSize;
    private long totalAnswerSize;
    private static final int TOP_FREQUENT_LENGTH = 3;
    private static final int PERCENTILE = 95;

    public StatisticsReport(ZonedDateTime from, ZonedDateTime to) {
        mostFrequentSources = new ArrayList<>();
        mostFrequentAnswerCode = new ArrayList<>();
        mostFrequentIp = new ArrayList<>();
        answerSize = new ArrayList<>();
        mostFrequentHttpMethod = new ArrayList<>();
        totalAnswerSize = 0L;
        requestNumber = 0L;
        if (from != null) {
            this.from = from.toString();
        }
        if (to != null) {
            this.to = to.toString();
        }
    }

    public void writeAdocFile(String outputPath) {
        generateReport(
            outputPath + "report.adoc",
            "=== General Information ===\n\n|===",
            "=== Frequency of Client Requests ===\n\n|===",
            "=== Answer Codes ===\n\n|===",
            "     ",
             "    ",
            "|===",
            "=== Most Frequent IP Addresses ===\n\n|==="
        );
    }

    public void writeMarkdownFile(String outputPath) {
        generateReport(
            outputPath + "report.md",
            "### General information\n",
            "### Frequency of client requests\n",
            "### Answer codes\n",
            "|:--------------:|:----------:|",
            "|:-------------:|:-----------:|:----:|",
            "",
            "### Most Frequent IP Addresses\n"
        );
    }

    private void generateGeneralInfo(
        FileWriter writer,
        String header,
        String separator,
        String endOfTable
    ) throws IOException {
        writer.write(String.format("""
            %s
            | Metrics                   | Value
            %s
            | File(-s)                  | %s
            | From                      | %s
            | To                        | %s
            | Number of requests        | %-15d
            | Average answer size       | %-13.2f
            | 95th percentile of answer | %-13.2f
            %s
            """,
            header, separator,
            fileNames, from, to,
            requestNumber,
            getAverageAnswerSize(),
            percentileSizeAnswer(PERCENTILE),
            endOfTable));
    }

    private void generateSourceFrequency(
        FileWriter writer,
        String header,
        String separator,
        String endOfTable
    ) throws IOException {
        List<Map.Entry<String, Integer>> frequentSource = getMostFrequentSources();

        if (frequentSource.isEmpty()) {
            writer.write(String.format("%s%nNo data available for source frequency.%n%s%n", header, endOfTable));
            return;
        }

        writer.write(String.format("""
            %s
            | Source                      | Frequency
            %s
            """, header, separator));

        int limit = Math.min(frequentSource.size(), TOP_FREQUENT_LENGTH);
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = frequentSource.get(i);
            String source = entry.getKey();
            int frequency = entry.getValue();

            writer.write(String.format("| %-27s | %-9d%n", source, frequency));
        }

        writer.write(endOfTable + "\n");
    }

    private void generateCodeInfo(
        FileWriter writer,
        String header,
        String separator,
        String endOfTable
    ) throws IOException {
        List<Map.Entry<Integer, Integer>> frequentCode = getMostFrequentAnswerCode();
        if (frequentCode.isEmpty()) {
            writer.write(String.format("%s%nNo data available for code frequency.%n%s%n", header, endOfTable));
            return;
        }

        writer.write(String.format("""
            %s
            | Code | Meaning                | Frequency
            %s
            """, header, separator));

        int limit = Math.min(frequentCode.size(), TOP_FREQUENT_LENGTH);
        for (int i = 0; i < limit; i++) {
            Map.Entry<Integer, Integer> entry = frequentCode.get(i);
            int code = entry.getKey();
            int frequency = entry.getValue();
            String meaning = HttpStatusCode.getStatusMessage(code);

            writer.write(String.format("| %-4d | %-27s | %-9d%n", code, meaning, frequency));
        }

        writer.write(endOfTable + "\n");
    }

    private void generateIpFrequency(
        FileWriter writer,
        String header,
        String separator,
        String endOfTable
    ) throws IOException {
        List<Map.Entry<String, Integer>> frequentIps = getMostFrequentIp();

        if (frequentIps.isEmpty()) {
            writer.write(String.format("%s%nNo data available for IP frequency.%n%s%n", header, endOfTable));
            return;
        }

        writer.write(String.format("""
            %s
            | IP Address               | Frequency
            %s
            """, header, separator));

        for (int i = 0; i < Math.min(frequentIps.size(), TOP_FREQUENT_LENGTH); i++) {
            Map.Entry<String, Integer> entry = frequentIps.get(i);
            writer.write(String.format("| %-27s | %-10d%n", entry.getKey(), entry.getValue()));
        }

        writer.write(endOfTable + "\n");
    }

    private void generateMethodFrequency(
        FileWriter writer,
        String header,
        String separator,
        String endOfTable
    ) throws IOException {
        List<Map.Entry<String, Integer>> methodFrequency = getHttpMethodFrequency();

        if (methodFrequency.isEmpty()) {
            writer.write(String.format("%s%nNo data available for HTTP methods.%n%s%n", header, endOfTable));
            return;
        }

        writer.write(String.format("""
            %s
            | HTTP Method | Frequency
            %s
            """, header, separator));

        for (int i = 0; i < Math.min(methodFrequency.size(), TOP_FREQUENT_LENGTH); i++) {
            Map.Entry<String, Integer> entry = methodFrequency.get(i);
            writer.write(String.format("| %-25s | %-10d%n", entry.getKey(), entry.getValue()));
        }

        writer.write(endOfTable + "\n");
    }

    @SuppressWarnings("checkstyle:parameternumber")
    private void generateReport(
        String filePath,
        String generalHeader,
        String requestHeader,
        String codeHeader,
        String separator,
        String codeSeparator,
        String endOfTable,
        String ipHeader
    ) {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            generateGeneralInfo(writer, generalHeader, separator, endOfTable);
            generateSourceFrequency(writer, requestHeader, separator, endOfTable);
            generateCodeInfo(writer, codeHeader, codeSeparator, endOfTable);
            generateIpFrequency(writer, ipHeader, separator, endOfTable);
            generateMethodFrequency(writer, requestHeader, separator, endOfTable);
            log.info("Writing report to {}", filePath);
        } catch (IOException e) {
            log.error("Error writing report to file: ", e);
        }
    }

    public List<Map.Entry<Integer, Integer>> getMostFrequentAnswerCode() {
        mostFrequentAnswerCode.sort((a, b) -> b.getValue() - a.getValue());
        return mostFrequentAnswerCode;
    }

    public List<Map.Entry<String, Integer>> getMostFrequentSources() {
        mostFrequentSources.sort((a, b) -> b.getValue() - a.getValue());
        return mostFrequentSources;
    }

    public List<Map.Entry<String, Integer>> getMostFrequentIp() {
        mostFrequentIp.sort((a, b) -> b.getValue() - a.getValue());
        return mostFrequentIp;
    }

    private List<Map.Entry<String, Integer>> getHttpMethodFrequency() {
        mostFrequentHttpMethod.sort((a, b) -> b.getValue() - a.getValue());
        return mostFrequentHttpMethod;
    }

    public double getAverageAnswerSize() {
        return requestNumber == 0L ? 0L : (double) totalAnswerSize / requestNumber;
    }

    public void incrementRequestNumber() {
        requestNumber++;
    }

    public void addSource(String request) {
        updateFrequency(mostFrequentSources, request);
    }

    public void addAnswerCode(int code) {
        updateFrequency(mostFrequentAnswerCode, code);
    }

    public void addIp(String ip) {
        updateFrequency(mostFrequentIp, ip);
    }

    public void addHttpMethod(String method) {
        updateFrequency(mostFrequentHttpMethod, method);
    }

    public void addAnswerSize(int size) {
        answerSize.add(size);
        totalAnswerSize += size;
    }

    public double percentileSizeAnswer(int percentile) {
        return percentile(answerSize, percentile);
    }

    private <T> void updateFrequency(List<Map.Entry<T, Integer>> list, T key) {
        Optional<Map.Entry<T, Integer>> existingEntry = list.stream()
            .filter(entry -> entry.getKey().equals(key))
            .findFirst();

        Map.Entry<T, Integer> entry = existingEntry.orElseGet(() -> Map.entry(key, 0));
        list.remove(entry);
        list.add(Map.entry(key, entry.getValue() + 1));
    }

    private double percentile(List<Integer> list, int percentile) {
        if (list.isEmpty()) {
            log.error("Empty list");
            return 0.0;
        }
        list.sort(Comparator.comparingInt(a -> a));
        return Quantiles.percentiles().index(percentile).compute(list);
    }

    public void addFileName(String fileName) {
        fileNames.add(fileName);
    }

    public void addFromTime(String from) {
        this.from = from;
    }

    public void addToTime(String to) {
        this.to = to;
    }
}
