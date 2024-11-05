package backend.academy;
import java.util.Map;
import java.util.HashMap;

public class HttpStatusCode {

    private static final Map<Integer, String> statusCodes = new HashMap<>();

    static {
        statusCodes.put(100, "Continue");
        statusCodes.put(101, "Switching Protocols");
        statusCodes.put(102, "Processing");

        statusCodes.put(200, "OK");
        statusCodes.put(201, "Created");
        statusCodes.put(202, "Accepted");
        statusCodes.put(203, "Non-Authoritative Information");
        statusCodes.put(204, "No Content");
        statusCodes.put(205, "Reset Content");
        statusCodes.put(206, "Partial Content");
        statusCodes.put(207, "Multi-Status");
        statusCodes.put(208, "Already Reported");
        statusCodes.put(226, "IM Used");

        statusCodes.put(300, "Multiple Choices");
        statusCodes.put(301, "Moved Permanently");
        statusCodes.put(302, "Found");
        statusCodes.put(303, "See Other");
        statusCodes.put(304, "Not Modified");
        statusCodes.put(305, "Use Proxy");
        statusCodes.put(307, "Temporary Redirect");
        statusCodes.put(308, "Permanent Redirect");

        statusCodes.put(400, "Bad Request");
        statusCodes.put(401, "Unauthorized");
        statusCodes.put(402, "Payment Required");
        statusCodes.put(403, "Forbidden");
        statusCodes.put(404, "Not Found");
        statusCodes.put(405, "Method Not Allowed");
        statusCodes.put(406, "Not Acceptable");
        statusCodes.put(407, "Proxy Authentication Required");
        statusCodes.put(408, "Request Timeout");
        statusCodes.put(409, "Conflict");
        statusCodes.put(410, "Gone");
        statusCodes.put(411, "Length Required");
        statusCodes.put(412, "Precondition Failed");
        statusCodes.put(413, "Payload Too Large");
        statusCodes.put(414, "URI Too Long");
        statusCodes.put(415, "Unsupported Media Type");
        statusCodes.put(416, "Range Not Satisfiable");
        statusCodes.put(417, "Expectation Failed");
        statusCodes.put(418, "I'm a teapot");  // Easter egg (RFC 2324)
        statusCodes.put(421, "Misdirected Request");
        statusCodes.put(422, "Unprocessable Entity");
        statusCodes.put(423, "Locked");
        statusCodes.put(424, "Failed Dependency");
        statusCodes.put(425, "Too Early");
        statusCodes.put(426, "Upgrade Required");
        statusCodes.put(428, "Precondition Required");
        statusCodes.put(429, "Too Many Requests");
        statusCodes.put(431, "Request Header Fields Too Large");
        statusCodes.put(451, "Unavailable For Legal Reasons");

        statusCodes.put(500, "Internal Server Error");
        statusCodes.put(501, "Not Implemented");
        statusCodes.put(502, "Bad Gateway");
        statusCodes.put(503, "Service Unavailable");
        statusCodes.put(504, "Gateway Timeout");
        statusCodes.put(505, "HTTP Version Not Supported");
        statusCodes.put(506, "Variant Also Negotiates");
        statusCodes.put(507, "Insufficient Storage");
        statusCodes.put(508, "Loop Detected");
        statusCodes.put(510, "Not Extended");
        statusCodes.put(511, "Network Authentication Required");
    }

    public static String getStatusMessage(int code) {
        return statusCodes.getOrDefault(code, "Unknown Status Code");
    }
}
