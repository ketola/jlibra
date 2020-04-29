package dev.jlibra.client;

import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bouncycastle.util.encoders.Hex;

import com.github.arteam.simplejsonrpc.client.JsonRpcClient;

import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.Event;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.Transaction;
import dev.jlibra.serialization.lcs.LCSSerializer;

public class LibraClient {

    private LibraJsonRpcClient libraJsonRpcClient;

    private LibraClient(LibraJsonRpcClient libraJsonRpcClient) {
        this.libraJsonRpcClient = libraJsonRpcClient;
    }

    public static LibraClientBuilder builder() {
        return new LibraClientBuilder();
    }

    public Account getAccountState(String address) {
        return libraJsonRpcClient.getAccountState(address);
    }

    public BlockMetadata getMetadata() {
        return libraJsonRpcClient.getMetadata();
    }

    public List<Transaction> getTransactions(long version, long limit,
            boolean includeEvents) {
        return libraJsonRpcClient.getTransactions(version, limit, includeEvents);
    }

    public Transaction getAccountTransaction(String address,
            long sequenceNumber,
            boolean includeEvents) {
        return libraJsonRpcClient.getAccountTransaction(address, sequenceNumber, includeEvents);
    }

    public List<Event> getEvents(String eventKey,
            long start,
            long limit) {
        return libraJsonRpcClient.getEvents(eventKey, start, limit);
    }

    public StateProof getStateProof(long knownVersion) {
        return libraJsonRpcClient.getStateProof(knownVersion);
    }

    public void submit(SignedTransaction signedTransaction) {
        libraJsonRpcClient
                .submit(Hex.toHexString(
                        LCSSerializer.create().serialize(signedTransaction, SignedTransaction.class).toArray()));
    }

    public static class LibraClientBuilder {

        private CloseableHttpClient httpClient;

        private String url;

        public LibraClientBuilder withHttpClient(CloseableHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public LibraClientBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public LibraClient build() {
            if (httpClient == null) {
                this.httpClient = HttpClients.createDefault();
            }
            return new LibraClient(new JsonRpcClient(new LibraJsonRpcTransport(httpClient, url))
                    .onDemand(LibraJsonRpcClient.class));
        }
    }

}
