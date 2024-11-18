package backend.academy;

public class StatisticsUpdater {
    private final StatisticsReport report;

    public StatisticsUpdater(StatisticsReport report) {
        this.report = report;
    }

    public void updateStatistics(NginxLog nginxLog) {
        report.incrementRequestNumber();
        report.addSource(nginxLog.httpRequest().uri());
        report.addAnswerCode(nginxLog.status());
        report.addAnswerSize(nginxLog.bytesSent());
        report.addIp(nginxLog.remoteAddress());
        report.addHttpMethod(nginxLog.httpRequest().method());
    }

    public void addFileName(String fileName) {
        report.addFileName(fileName);
    }

    public void addFromTime(String from) {
        report.addFromTime(from);
    }

    public void addToTime(String to) {
        report.addToTime(to);
    }
}
