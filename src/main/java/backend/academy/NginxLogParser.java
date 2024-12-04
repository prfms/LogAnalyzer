package backend.academy;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NginxLogParser {
    private static final Pattern LOG_PATTERN =
        Pattern.compile(
            "^(\\S+) - (\\S*) \\[(.*?)\\] \"(.*?) (.*?) (.*?)\" (\\d{3}) (\\d+) \"(.*?)\" \"(.*?)\"$");

    public NginxLog parse(String singleLog) {
        final int REMOTE_ADDRESS_GROUP = 1;
        final int REMOTE_USER_GROUP = 2;
        final int TIME_LOCAL_GROUP = 3;
        final int REQUEST_METHOD_GROUP = 4;
        final int REQUEST_URI_GROUP = 5;
        final int REQUEST_PROTOCOL_GROUP = 6;
        final int STATUS_GROUP = 7;
        final int BYTES_SENT_GROUP = 8;
        final int REFERER_GROUP = 9;
        final int USER_AGENT_GROUP = 10;

        Matcher matcher = LOG_PATTERN.matcher(singleLog);

        if (matcher.find()) {
            NginxLog nginxLog = new NginxLog();
            nginxLog.remoteAddress(matcher.group(REMOTE_ADDRESS_GROUP));
            nginxLog.remoteUser(matcher.group(REMOTE_USER_GROUP).isEmpty()
                || "-".equals(matcher.group(REMOTE_USER_GROUP)) ? null
                : matcher.group(REMOTE_USER_GROUP));

            String timeString = matcher.group(TIME_LOCAL_GROUP);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/LLL/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            try {
                nginxLog.localTime(ZonedDateTime.parse(timeString, formatter));
            } catch (DateTimeParseException e) {
                log.error("Incorrect time format {}", timeString, e);
            }

            nginxLog.httpRequest(new NginxLog.HttpRequest(
                matcher.group(REQUEST_METHOD_GROUP),
                matcher.group(REQUEST_URI_GROUP),
                matcher.group(REQUEST_PROTOCOL_GROUP)
            ));

            nginxLog.status(Integer.parseInt(matcher.group(STATUS_GROUP)));
            nginxLog.bytesSent(Integer.parseInt(matcher.group(BYTES_SENT_GROUP)));
            nginxLog.httpReferer("-".equals(matcher.group(REFERER_GROUP)) ? null : matcher.group(REFERER_GROUP));
            nginxLog.httpUserAgent(matcher.group(USER_AGENT_GROUP));

            return nginxLog;
        } else {
            throw new IllegalArgumentException("String does not match Nginx pattern");
        }
    }
}
