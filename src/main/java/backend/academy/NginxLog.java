package backend.academy;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NginxLog {
    private String remoteAddress;
    private String remoteUser;
    private ZonedDateTime localTime;
    private HttpRequest httpRequest;
    private int status;
    private int bytesSent;
    private String httpReferer;
    private String httpUserAgent;

    public record HttpRequest(String method, String uri, String version) {}
}
