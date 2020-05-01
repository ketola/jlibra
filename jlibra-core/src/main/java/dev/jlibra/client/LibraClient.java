package dev.jlibra.client;

import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bouncycastle.util.encoders.Hex;

import com.github.arteam.simplejsonrpc.client.JsonRpcClient;

import dev.jlibra.LibraRuntimeException;
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
        try {
            return libraJsonRpcClient.getAccountState(address);
        } catch (Exception e) {
            throw new LibraRuntimeException("getAccountState failed", e);
        }
    }

    public BlockMetadata getMetadata() {
        try {
            return libraJsonRpcClient.getMetadata();
        } catch (Exception e) {
            throw new LibraRuntimeException("getMetadata failed", e);
        }
    }

    public List<Transaction> getTransactions(long version, long limit,
            boolean includeEvents) {
        try {
            return libraJsonRpcClient.getTransactions(version, limit, includeEvents);
        } catch (Exception e) {
            throw new LibraRuntimeException("getTransactions failed", e);
        }
    }

    public Transaction getAccountTransaction(String address,
            long sequenceNumber,
            boolean includeEvents) {
        try {
            return libraJsonRpcClient.getAccountTransaction(address, sequenceNumber, includeEvents);
        } catch (Exception e) {
            throw new LibraRuntimeException("getAccountTransaction failed", e);
        }
    }

    public List<Event> getEvents(String eventKey,
            long start,
            long limit) {
        try {
            return libraJsonRpcClient.getEvents(eventKey, start, limit);
        } catch (Exception e) {
            throw new LibraRuntimeException("getEvents failed", e);
        }
    }

    public StateProof getStateProof(long knownVersion) {
        try {
            return libraJsonRpcClient.getStateProof(knownVersion);
        } catch (Exception e) {
            throw new LibraRuntimeException("submit failed", e);
        }
    }

    public void submit(SignedTransaction signedTransaction) {
        try {
            libraJsonRpcClient
                    .submit(Hex.toHexString(
                            LCSSerializer.create().serialize(signedTransaction, SignedTransaction.class).toArray()));
        } catch (Exception e) {
            throw new LibraRuntimeException("submit failed", e);
        }
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
