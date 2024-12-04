package backend.academy;

import backend.academy.logSource.LogSource;
import java.util.List;
import java.util.stream.Stream;

public class LogProcessor {
    private final LogSource logSource;
    private final NginxLogParser logParser;
    private final LogFilter logFilter;
    private final StatisticsUpdater statisticsUpdater;

    public LogProcessor(
        LogSource logSource,
        NginxLogParser logParser,
        LogFilter logFilter,
        StatisticsUpdater statisticsUpdater
    ) {
        this.logSource = logSource;
        this.logParser = logParser;
        this.logFilter = logFilter;
        this.statisticsUpdater = statisticsUpdater;
    }

    public void process() {
        try (Stream<String> logLines = logSource.getLogs()) {
            logFilter.filter(logLines.map(logParser::parse))
                .forEach(statisticsUpdater::updateStatistics);
        }

        List<String> sourceNames = logSource.getName();
        for (String sourceName : sourceNames) {
            statisticsUpdater.addFileName(sourceName);
        }
    }
}
