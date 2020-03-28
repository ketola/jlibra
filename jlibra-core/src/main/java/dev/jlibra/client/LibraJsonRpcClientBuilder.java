package dev.jlibra.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.arteam.simplejsonrpc.client.JsonRpcClient;

public class LibraJsonRpcClientBuilder {

    public static LibraJsonRpcClientBuilder builder() {
        return new LibraJsonRpcClientBuilder();
    }

    private CloseableHttpClient httpClient;

    private String url;

    public LibraJsonRpcClientBuilder withHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public LibraJsonRpcClientBuilder withUrl(String url) {
        this.url = url;
        return this;
    }

    public LibraJsonRpcClient build() {
        if (httpClient == null) {
            this.httpClient = HttpClients.createDefault();
        }
        return new JsonRpcClient(new LibraJsonRpcTransport(httpClient, url))
                .onDemand(LibraJsonRpcClient.class);
    }
}
