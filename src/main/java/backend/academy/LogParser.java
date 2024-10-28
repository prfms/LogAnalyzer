package backend.academy;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    private static final String LOG_PATTERN =
        "^(\\S+) - (\\S*) \\[(.*?)\\] \"(.*?)\" (\\d{3}) (\\d+) \"(.*?)\" \"(.*?)\"$";

    private static final Pattern pattern = Pattern.compile(LOG_PATTERN);
    private Logger logger;

    public NginxLog parse(String log) {
        Matcher matcher = pattern.matcher(log);

        if (matcher.find()) {
            NginxLog nginxLog = new NginxLog();
            nginxLog.remoteAddress(matcher.group(1));
            nginxLog.remoteUser(matcher.group(2).isEmpty() || matcher.group(2).equals("-") ? null : matcher.group(2));

            String timeString = matcher.group(3);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            try {
                nginxLog.localTime(ZonedDateTime.parse(timeString, formatter));
            } catch (DateTimeParseException e) {
                logger.log(Level.SEVERE, "Incorrect time format: f" + timeString, e);
            }

            nginxLog.request(matcher.group(4));
            nginxLog.status(Integer.parseInt(matcher.group(5)));
            nginxLog.bytesSent(Integer.parseInt(matcher.group(6)));
            nginxLog.httpReferer(matcher.group(7).equals("-") ? null : matcher.group(7));
            nginxLog.httpUserAgent(matcher.group(8));

            return nginxLog;
        } else {
            throw new IllegalArgumentException("Строка не соответствует формату лога Nginx.");
        }
    }
}
