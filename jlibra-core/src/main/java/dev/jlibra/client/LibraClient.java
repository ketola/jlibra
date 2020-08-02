package dev.jlibra.client;

import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bouncycastle.util.encoders.Hex;

import com.github.arteam.simplejsonrpc.client.JsonRpcClient;
import com.github.arteam.simplejsonrpc.client.exception.JsonRpcException;

import dev.jlibra.AccountAddress;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.CurrencyInfo;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.Transaction;
import dev.jlibra.client.views.event.Event;
import dev.jlibra.serialization.lcs.LCSSerializer;
import dev.jlibra.transaction.SignedTransaction;

public class LibraClient {

    private LibraJsonRpcClient libraJsonRpcClient;

    private LibraClient(LibraJsonRpcClient libraJsonRpcClient) {
        this.libraJsonRpcClient = libraJsonRpcClient;
    }

    public static LibraClientBuilder builder() {
        return new LibraClientBuilder();
    }

    public Account getAccount(AccountAddress accountAddress) {
        try {
            return libraJsonRpcClient.getAccount(Hex.toHexString(accountAddress.toArray()));
        } catch (JsonRpcException e) {
            throw new LibraServerErrorException(e.getErrorMessage().getCode(), e.getErrorMessage().getMessage());
        } catch (Exception e) {
            throw new LibraRuntimeException("getAccountState failed", e);
        }
    }

    public BlockMetadata getMetadata() {
        try {
            return libraJsonRpcClient.getMetadata();
        } catch (JsonRpcException e) {
            throw new LibraServerErrorException(e.getErrorMessage().getCode(), e.getErrorMessage().getMessage());
        } catch (Exception e) {
            throw new LibraRuntimeException("getMetadata failed", e);
        }
    }

    public List<Transaction> getTransactions(long version, long limit,
            boolean includeEvents) {
        try {
            return libraJsonRpcClient.getTransactions(version, limit, includeEvents);
        } catch (JsonRpcException e) {
            throw new LibraServerErrorException(e.getErrorMessage().getCode(), e.getErrorMessage().getMessage());
        } catch (Exception e) {
            throw new LibraRuntimeException("getTransactions failed", e);
        }
    }

    public Transaction getAccountTransaction(AccountAddress accountAddress,
            long sequenceNumber,
            boolean includeEvents) {
        try {
            return libraJsonRpcClient.getAccountTransaction(Hex.toHexString(accountAddress.toArray()), sequenceNumber,
                    includeEvents);
        } catch (JsonRpcException e) {
            throw new LibraServerErrorException(e.getErrorMessage().getCode(), e.getErrorMessage().getMessage());
        } catch (Exception e) {
            throw new LibraRuntimeException("getAccountTransaction failed", e);
        }
    }

    public List<Event> getEvents(String eventKey,
            long start,
            long limit) {
        try {
            return libraJsonRpcClient.getEvents(eventKey, start, limit);
        } catch (JsonRpcException e) {
            throw new LibraServerErrorException(e.getErrorMessage().getCode(), e.getErrorMessage().getMessage());
        } catch (Exception e) {
            throw new LibraRuntimeException("getEvents failed", e);
        }
    }

    public StateProof getStateProof(long knownVersion) {
        try {
            return libraJsonRpcClient.getStateProof(knownVersion);
        } catch (JsonRpcException e) {
            throw new LibraServerErrorException(e.getErrorMessage().getCode(), e.getErrorMessage().getMessage());
        } catch (Exception e) {
            throw new LibraRuntimeException("getStateProof failed", e);
        }
    }

    public List<CurrencyInfo> currenciesInfo() {
        try {
            return libraJsonRpcClient.currenciesInfo();
        } catch (JsonRpcException e) {
            throw new LibraServerErrorException(e.getErrorMessage().getCode(), e.getErrorMessage().getMessage());
        } catch (Exception e) {
            throw new LibraRuntimeException("currenciesInfo failed", e);
        }
    }

    public void submit(SignedTransaction signedTransaction) {
        try {
            libraJsonRpcClient
                    .submit(Hex.toHexString(
                            LCSSerializer.create().serialize(signedTransaction, SignedTransaction.class).toArray()));
        } catch (JsonRpcException e) {
            throw new LibraServerErrorException(e.getErrorMessage().getCode(), e.getErrorMessage().getMessage());
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
