package dev.jlibra.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.arteam.simplejsonrpc.client.Transport;

import dev.jlibra.LibraRuntimeException;

public class LibraJsonRpcTransport implements Transport {

    private static final Logger log = LoggerFactory.getLogger(LibraJsonRpcTransport.class);

    private static final String USER_AGENT = "JLibra";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private HttpClient httpClient;

    private String url;

    public LibraJsonRpcTransport(HttpClient httpClient, String url) {
        this.httpClient = httpClient;
        this.url = url;
    }

    @Override
    public String pass(String body) throws IOException {
        log.debug("Request: {}", body);
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", CONTENT_TYPE_JSON)
                .header("User-Agent", USER_AGENT)
                .uri(URI.create(url))
                .POST(BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            String responseBody = response.body();
            log.debug("Response: {}", responseBody);
            return responseBody;
        } catch (IOException | InterruptedException e) {
            throw new LibraRuntimeException("Http request failed", e);
        }
    }
}
