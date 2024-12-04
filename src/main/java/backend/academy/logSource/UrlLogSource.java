package backend.academy.logSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Stream;

public class UrlLogSource implements LogSource {
    private final URI uri;

    public UrlLogSource(String url) {
        try {
            this.uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }

    @Override
    public Stream<String> getLogs() {
        try ( HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new BufferedReader(new StringReader(response.body())).lines();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error reading logs from URL: " + uri, e);
        }
    }

    @Override
    public List<String> getName() {
        return List.of(uri.toString());
    }
}
