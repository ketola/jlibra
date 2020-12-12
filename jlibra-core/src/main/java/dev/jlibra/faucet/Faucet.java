package dev.jlibra.faucet;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import dev.jlibra.AuthenticationKey;
import dev.jlibra.DiemRuntimeException;

public class Faucet {

    private HttpClient httpClient;

    private String url;

    private Faucet(HttpClient httpClient, String url) {
        this.httpClient = httpClient;
        this.url = url;
    }

    public void mint(AuthenticationKey authenticationKey, long amountInMicroLibras, String currencyCode) {
        HttpRequest request = HttpRequest.newBuilder()
                .header("User-Agent", "JLibra")
                .POST(BodyPublishers.ofString(""))
                .uri(URI.create(url + String.format("?amount=%s&auth_key=%s&currency_code=%s",
                        Long.toString(amountInMicroLibras), authenticationKey.toString(), currencyCode)))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new DiemRuntimeException(String.format("Mint failed. Status code: %d, message: %s",
                        response.statusCode(), response.body()));
            }
        } catch (IOException | InterruptedException e) {
            throw new DiemRuntimeException("Mint failed", e);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private static final String DEFAULT_URL = "http://testnet.diem.com/mint";

        private HttpClient httpClient;

        private String url;

        private Builder() {
        }

        public Builder withHttpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Faucet build() {
            if (httpClient == null) {
                this.httpClient = HttpClient.newBuilder()
                        .version(Version.HTTP_2)
                        .build();
            }
            if (url == null) {
                this.url = DEFAULT_URL;
            }

            return new Faucet(httpClient, url);
        }
    }
}
