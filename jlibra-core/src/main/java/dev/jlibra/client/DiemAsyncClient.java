package dev.jlibra.client;

import static java.net.http.HttpClient.Version.HTTP_2;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.AccountAddress;
import dev.jlibra.DiemRuntimeException;
import dev.jlibra.client.jsonrpc.BatchRequest;
import dev.jlibra.client.jsonrpc.DiemJsonRpcClient;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.CurrencyInfo;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.event.Event;
import dev.jlibra.client.views.transaction.Transaction;
import dev.jlibra.serialization.bcs.BCSSerializer;
import dev.jlibra.transaction.SignedTransaction;

public class DiemAsyncClient {

    private DiemJsonRpcClient diemJsonRpcClient;

    private DiemAsyncClient(DiemJsonRpcClient diemJsonRpcClient) {
        this.diemJsonRpcClient = diemJsonRpcClient;
    }

    public static DiemClientBuilder builder() {
        return new DiemClientBuilder();
    }

    public CompletableFuture<Optional<Account>> getAccount(AccountAddress accountAddress) {
        try {
            return diemJsonRpcClient.getAccount(Hex.toHexString(accountAddress.toArray()));
        } catch (Exception e) {
            throw new DiemRuntimeException("getAccountState failed", e);
        }
    }

    public CompletableFuture<Optional<BlockMetadata>> getMetadata() {
        try {
            return diemJsonRpcClient.getMetadata();
        } catch (Exception e) {
            throw new DiemRuntimeException("getMetadata failed", e);
        }
    }

    public CompletableFuture<Optional<List<Transaction>>> getTransactions(long version, long limit,
            boolean includeEvents) {
        try {
            return diemJsonRpcClient.getTransactions(version, limit, includeEvents);
        } catch (Exception e) {
            throw new DiemRuntimeException("getTransactions failed", e);
        }
    }

    public CompletableFuture<Optional<Transaction>> getAccountTransaction(AccountAddress accountAddress,
            long sequenceNumber,
            boolean includeEvents) {
        try {
            return diemJsonRpcClient.getAccountTransaction(Hex.toHexString(accountAddress.toArray()), sequenceNumber,
                    includeEvents);
        } catch (Exception e) {
            throw new DiemRuntimeException("getAccountTransaction failed", e);
        }
    }

    public CompletableFuture<Optional<List<Transaction>>> getAccountTransactions(AccountAddress accountAddress,
            long start,
            long limit,
            boolean includeEvents) {
        try {
            return diemJsonRpcClient.getAccountTransactions(Hex.toHexString(accountAddress.toArray()), start, limit,
                    includeEvents);
        } catch (Exception e) {
            throw new DiemRuntimeException("getAccountTransactions failed", e);
        }
    }

    public CompletableFuture<Optional<List<Event>>> getEvents(String eventKey,
            long start,
            long limit) {
        try {
            return diemJsonRpcClient.getEvents(eventKey, start, limit);
        } catch (Exception e) {
            throw new DiemRuntimeException("getEvents failed", e);
        }
    }

    public CompletableFuture<Optional<StateProof>> getStateProof(long knownVersion) {
        try {
            return diemJsonRpcClient.getStateProof(knownVersion);
        } catch (Exception e) {
            throw new DiemRuntimeException("getStateProof failed", e);
        }
    }

    public CompletableFuture<Optional<List<CurrencyInfo>>> currenciesInfo() {
        try {
            return diemJsonRpcClient.getCurrencies();
        } catch (Exception e) {
            throw new DiemRuntimeException("currenciesInfo failed", e);
        }
    }

    public CompletableFuture<Optional<Void>> submit(SignedTransaction signedTransaction) {
        try {
            return diemJsonRpcClient
                    .submit(Hex.toHexString(
                            BCSSerializer.create().serialize(signedTransaction, SignedTransaction.class).toArray()));
        } catch (Exception e) {
            throw new DiemRuntimeException("submit failed", e);
        }
    }

    public BatchRequest newBatchRequest() {
        return diemJsonRpcClient.newBatchRequest();
    }

    public static class DiemClientBuilder {

        private HttpClient httpClient;

        private String url;

        public DiemClientBuilder withHttpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public DiemClientBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public DiemAsyncClient build() {
            if (httpClient == null) {
                this.httpClient = HttpClient.newBuilder()
                        .version(HTTP_2)
                        .build();
            }
            return new DiemAsyncClient(
                    new DiemJsonRpcClient(url, httpClient, () -> UUID.randomUUID().toString()));
        }
    }
}
