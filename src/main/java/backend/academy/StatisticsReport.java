package backend.academy;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter @Setter
public class StatisticsReport {
    private static final Logger log = LoggerFactory.getLogger(StatisticsReport.class);
    private long requestNumber;
    private List<Map.Entry<String, Integer>> mostFrequentSources;
    private List<Map.Entry<Integer, Integer>> mostFrequentAnswerCode;
    private List<Integer> answerSize;
    private long totalAnswerSize;

    public StatisticsReport() {
        mostFrequentSources = new ArrayList<>();
        mostFrequentAnswerCode = new ArrayList<>();
        answerSize = new ArrayList<>();
        totalAnswerSize = 0L;
        requestNumber = 0L;
    }

    public void buildMarkdownReport() {
        File file = new File("src/main/resources/markdown.md");
        try {
            try (FileWriter fileWriter = new FileWriter(file)) {
                String heading1 = "### General information\n";
                String generalInfo = String.format("""
                    | Metrics                   | Value          |
                    |:-------------------------:|:--------------:|
                    | Number of requests        | %15d           |
                    | Average answer size       | %13.2f         |
                    | 95th percentile of answer | %13.2f         |
                    """, requestNumber, getAverageAnswerSize(), percentileSizeAnswer(95));

                String heading2 = "### Frequency of client requests\n";
                String requestInfo = String.format("""
                    | Requests          | Quantity       |
                    |:-----------------:|:--------------:|
                    |%15s               | %15s           |
                    |%15s               | %15s           |
                    |%15s               | %15s           |
                    """, getMostFrequentSources().getFirst().getKey(), getMostFrequentSources().getFirst().getValue(),
                    getMostFrequentSources().get(1).getKey(), getMostFrequentSources().get(1).getValue(),
                    getMostFrequentSources().get(2).getKey(), getMostFrequentSources().get(2).getValue());

                fileWriter.write(heading1);
                fileWriter.write(generalInfo);
                fileWriter.write(heading2);
                fileWriter.write(requestInfo);

                fileWriter.flush();
            }
        } catch (IOException e) {
            log.error("IOException while writing to file", e);
        }

    }
    public List<Map.Entry<Integer, Integer>> getMostFrequentAnswerCode() {
        mostFrequentAnswerCode.sort((a,b) -> b.getValue() - a.getValue());
        return mostFrequentAnswerCode;
    }

    public List<Map.Entry<String, Integer>> getMostFrequentSources() {
        mostFrequentSources.sort((a,b) -> b.getValue() - a.getValue());
        return mostFrequentSources;
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

    public void addAnswerSize(int size) {
        answerSize.add(size);
        totalAnswerSize += size;
    }

    public double percentileSizeAnswer(double percentile) {
        return percentile(answerSize, percentile);
    }

    private <T> void updateFrequency(List<Map.Entry<T, Integer>> list, T key) {
        Optional<Map.Entry<T, Integer>> existingEntry = list.stream()
            .filter(entry -> entry.getKey().equals(key))
            .findFirst();

        if (existingEntry.isPresent()) {
            Map.Entry<T, Integer> entry = existingEntry.get();
            list.remove(entry);
            list.add(Map.entry(key, entry.getValue() + 1));
        } else {
            list.add(Map.entry(key, 1));
        }
    }

    private double percentile(List<Integer> list, double percentile) {
        list.sort(Comparator.comparingInt(a -> a));
        //double p = Quantiles.percentiles().index(95).compute(list);
        int index = (int) Math.ceil((percentile / 100.0) * list.size()) - 1;
        //return p;
        return list.get(index);
    }
}
//95-й прецентиль — это такое число, что 95% элементов массива меньше или равны этому числу.
