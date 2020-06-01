package dev.jlibra.faucet;

import static org.apache.http.HttpHeaders.USER_AGENT;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import dev.jlibra.AuthenticationKey;
import dev.jlibra.LibraRuntimeException;

public class Faucet {

    private CloseableHttpClient httpClient;

    private String url;

    private Faucet(CloseableHttpClient httpClient, String url) {
        this.httpClient = httpClient;
        this.url = url;
    }

    public void mint(AuthenticationKey authenticationKey, long amountInMicroLibras, String currencyCode) {
        try {
            URIBuilder builder = new URIBuilder(url)
                    .setParameter("amount", Long.toString(amountInMicroLibras))
                    .setParameter("auth_key", authenticationKey.toString())
                    .setParameter("currency_code", currencyCode);
            HttpPost httpPost = new HttpPost(builder.build().toString());
            httpPost.addHeader(new BasicHeader(USER_AGENT, "JLibra"));
            httpPost.setEntity(new StringEntity(""));

            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new LibraRuntimeException(String.format("Mint failed. Status code: %d, message: %s",
                        response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase()));
            }
        } catch (IOException | URISyntaxException e) {
            throw new LibraRuntimeException("Mint failed", e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private static final String DEFAULT_URL = "http://faucet.testnet.libra.org";

        private CloseableHttpClient httpClient;

        private String url;

        private Builder() {
        }

        public Builder withHttpClient(CloseableHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Faucet build() {
            if (httpClient == null) {
                this.httpClient = HttpClients.createDefault();
            }
            if (url == null) {
                this.url = DEFAULT_URL;
            }

            return new Faucet(httpClient, url);
        }
    }
}
