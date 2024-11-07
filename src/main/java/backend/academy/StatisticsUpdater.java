package backend.academy;

public class StatisticsUpdater {
    private final StatisticsReport report;

    public StatisticsUpdater(StatisticsReport report) {
        this.report = report;
    }

    public void updateStatistics(NginxLog nginxLog) {
        report.incrementRequestNumber();
        report.addSource(nginxLog.requestSource());
        report.addAnswerCode(nginxLog.status());
        report.addAnswerSize(nginxLog.bytesSent());
    }
}
