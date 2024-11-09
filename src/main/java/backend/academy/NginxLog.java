package backend.academy;

import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

@Getter @Setter
public class NginxLog {
    private String remoteAddress;
    private String remoteUser;
    private ZonedDateTime localTime;
    private String requestMethod;
    private String requestSource;
    private int status;
    private int bytesSent;
    private String httpReferer;
    private String httpUserAgent;
}
