package tests;

import backend.academy.LogParser;
import backend.academy.NginxLog;
import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ParserWorksTest {
    @Test
    public void ParserWorks() {
        LogParser parser = new LogParser(null, null, null, null, null);
        String validLog = "127.0.0.1 - - [12/Dec/2024:06:25:24 +0000] \"GET /index.html HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\"";

        // Act
        NginxLog result = parser.parse(validLog);

        // Assert
        assertNotNull(result);
        assertEquals("127.0.0.1", result.remoteAddress());
        assertEquals(ZonedDateTime.parse("2024-12-12T06:25:24+00:00", DateTimeFormatter.ISO_ZONED_DATE_TIME), result.localTime());
        assertEquals("GET", result.httpRequest().method());
        assertEquals("/index.html", result.httpRequest().uri());
        assertEquals("HTTP/1.1", result.httpRequest().version());
        assertEquals(200, result.status());
        assertEquals(1234, result.bytesSent());
        assertNull(result.httpReferer());
        assertEquals("Mozilla/5.0", result.httpUserAgent());
    }
}
