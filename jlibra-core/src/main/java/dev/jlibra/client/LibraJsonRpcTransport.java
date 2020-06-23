package dev.jlibra.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.arteam.simplejsonrpc.client.Transport;

public class LibraJsonRpcTransport implements Transport {

    private static final Logger log = LoggerFactory.getLogger(LibraJsonRpcTransport.class);

    private static final String USER_AGENT = "JLibra";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private CloseableHttpClient httpClient;

    private String url;

    public LibraJsonRpcTransport(CloseableHttpClient httpClient, String url) {
        this.httpClient = httpClient;
        this.url = url;
    }

    @Override
    public String pass(String request) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(request, UTF_8));
        post.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON);
        post.setHeader(HttpHeaders.USER_AGENT, USER_AGENT);

        log.debug("Request: {}", log.isDebugEnabled() ? EntityUtils.toString(post.getEntity()) : "");
        try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
            String response = EntityUtils.toString(httpResponse.getEntity(), UTF_8);
            log.debug("Response: {}", response);
            return response;
        }
    }
}
