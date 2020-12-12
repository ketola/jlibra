package dev.jlibra.integrationtest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Security;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.jlibra.client.DiemClient;
import dev.jlibra.client.jsonrpc.BatchRequest;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.CurrencyInfo;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.transaction.Transaction;

public class BatchRequestIntegrationTest {

    private DiemClient client;

    @BeforeEach
    public void setUp() {
        Security.addProvider(new BouncyCastleProvider());
        client = DiemClient.builder()
                .withUrl("https://testnet.diem.com/v1")
                .build();
    }

    @Test
    public void testBatchRequest() throws Exception {
        BatchRequest batchRequest = client.newBatchRequest();

        CompletableFuture<List<CurrencyInfo>> currenciesInfo = batchRequest.getCurrencies();
        CompletableFuture<BlockMetadata> metadata = batchRequest.getMetadata();
        CompletableFuture<Optional<StateProof>> stateProof = batchRequest.getStateProof(1L);
        CompletableFuture<List<Transaction>> transactions = batchRequest.getTransactions(1, 10, true);

        batchRequest.execute();

        assertNotNull(currenciesInfo.get());
        assertNotNull(metadata.get());
        assertTrue(stateProof.get().isPresent());
        assertNotNull(transactions.get());
    }
}
