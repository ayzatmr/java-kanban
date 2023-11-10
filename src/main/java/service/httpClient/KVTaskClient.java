package service.httpClient;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private String token;

    private final String url;
    private final HttpClient client = HttpClient.newHttpClient();
    private static final Logger log = LoggerFactory.getLogger(KVServer.class);

    public KVTaskClient(String url) {
        this.url = url;
        URI uri = URI.create(url + "/register");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.token = response.body();
        } catch (IOException | InterruptedException e) {
            log.error("Error when sending http request to url={}", uri, e);
        }
    }


    public void put(String key, String json) {
        URI uri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setPath("/save" + "/" + key);
            uriBuilder.setParameter("API_TOKEN", token);
            uri = uriBuilder.build().toURL().toURI();

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();

            HttpRequest request = requestBuilder
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .uri(uri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .build();


            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.error("Error when sending http request to url={}", uri);
        }
    }

    public String load(String key) {
        URI uri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setPath("/load" + "/" + key);
            uriBuilder.setParameter("API_TOKEN", token);
            uri = uriBuilder.build().toURL().toURI();

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            HttpRequest request = requestBuilder
                    .GET()
                    .uri(uri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.error("Error when sending http request to url={}", uri);
            return null;
        }
    }

    public void clear() {
        URI uri = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setPath("/clear");
            uriBuilder.setParameter("API_TOKEN", token);
            uri = uriBuilder.build().toURL().toURI();

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
            HttpRequest request = requestBuilder
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .uri(uri)
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            log.error("Error when sending http request to url={}", uri);
        }
    }
}
