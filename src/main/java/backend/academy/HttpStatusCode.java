package backend.academy;

import java.util.HashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("checkstyle:magicnumber")
public class HttpStatusCode {

    private static final Map<Integer, String> STATUS_CODES = new HashMap<>();

    static {
        STATUS_CODES.put(100, "Continue");
        STATUS_CODES.put(101, "Switching Protocols");
        STATUS_CODES.put(102, "Processing");

        STATUS_CODES.put(200, "OK");
        STATUS_CODES.put(201, "Created");
        STATUS_CODES.put(202, "Accepted");
        STATUS_CODES.put(203, "Non-Authoritative Information");
        STATUS_CODES.put(204, "No Content");
        STATUS_CODES.put(205, "Reset Content");
        STATUS_CODES.put(206, "Partial Content");
        STATUS_CODES.put(207, "Multi-Status");
        STATUS_CODES.put(208, "Already Reported");
        STATUS_CODES.put(226, "IM Used");

        STATUS_CODES.put(300, "Multiple Choices");
        STATUS_CODES.put(301, "Moved Permanently");
        STATUS_CODES.put(302, "Found");
        STATUS_CODES.put(303, "See Other");
        STATUS_CODES.put(304, "Not Modified");
        STATUS_CODES.put(305, "Use Proxy");
        STATUS_CODES.put(307, "Temporary Redirect");
        STATUS_CODES.put(308, "Permanent Redirect");

        STATUS_CODES.put(400, "Bad Request");
        STATUS_CODES.put(401, "Unauthorized");
        STATUS_CODES.put(402, "Payment Required");
        STATUS_CODES.put(403, "Forbidden");
        STATUS_CODES.put(404, "Not Found");
        STATUS_CODES.put(405, "Method Not Allowed");
        STATUS_CODES.put(406, "Not Acceptable");
        STATUS_CODES.put(407, "Proxy Authentication Required");
        STATUS_CODES.put(408, "Request Timeout");
        STATUS_CODES.put(409, "Conflict");
        STATUS_CODES.put(410, "Gone");
        STATUS_CODES.put(411, "Length Required");
        STATUS_CODES.put(412, "Precondition Failed");
        STATUS_CODES.put(413, "Payload Too Large");
        STATUS_CODES.put(414, "URI Too Long");
        STATUS_CODES.put(415, "Unsupported Media Type");
        STATUS_CODES.put(416, "Range Not Satisfiable");
        STATUS_CODES.put(417, "Expectation Failed");
        STATUS_CODES.put(418, "I'm a teapot");  // Easter egg (RFC 2324)
        STATUS_CODES.put(421, "Misdirected Request");
        STATUS_CODES.put(422, "Unprocessable Entity");
        STATUS_CODES.put(423, "Locked");
        STATUS_CODES.put(424, "Failed Dependency");
        STATUS_CODES.put(425, "Too Early");
        STATUS_CODES.put(426, "Upgrade Required");
        STATUS_CODES.put(428, "Precondition Required");
        STATUS_CODES.put(429, "Too Many Requests");
        STATUS_CODES.put(431, "Request Header Fields Too Large");
        STATUS_CODES.put(451, "Unavailable For Legal Reasons");

        STATUS_CODES.put(500, "Internal Server Error");
        STATUS_CODES.put(501, "Not Implemented");
        STATUS_CODES.put(502, "Bad Gateway");
        STATUS_CODES.put(503, "Service Unavailable");
        STATUS_CODES.put(504, "Gateway Timeout");
        STATUS_CODES.put(505, "HTTP Version Not Supported");
        STATUS_CODES.put(506, "Variant Also Negotiates");
        STATUS_CODES.put(507, "Insufficient Storage");
        STATUS_CODES.put(508, "Loop Detected");
        STATUS_CODES.put(510, "Not Extended");
        STATUS_CODES.put(511, "Network Authentication Required");
    }

    public static String getStatusMessage(int code) {
        return STATUS_CODES.getOrDefault(code, "Unknown Status Code");
    }
}
