package backend.academy;

import lombok.experimental.UtilityClass;
import java.io.IOException;
import java.net.URISyntaxException;

@UtilityClass
public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
// .\src\main\resources\logs_samples.txt
        StatisticsReport report = new StatisticsReport();
        StatisticsUpdater calculator = new StatisticsUpdater(report);
        LogParser parser = new LogParser(calculator);

       // parser.parseURL("https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs");
        parser.parseFile("src/main/resources/logs_samples.txt");
        System.out.println("Количество запросов: " + report.requestNumber());
        System.out.println("Средний размер ответа: " + report.getAverageAnswerSize());
        System.out.println("Самые частые источники запросов: " + report.getMostFrequentSources());
        System.out.println("Самые частые коды ответа: " + report.getMostFrequentAnswerCode());
        System.out.println("95% размера ответа сервера: " + report.percentileSizeAnswer(95));
        report.writeMarkdownFile();
        report.writeAdocFile();
        //NginxLog nginxLog = logParser.parse("93.180.71.3 - - [17/May/2015:08:05:32 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"");
    }
}
