package dev.jlibra.example;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.client.LibraClient;
import dev.jlibra.client.jsonrpc.BatchRequest;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.transaction.Transaction;

public class BatchRequestExample {

    private static final Logger logger = LoggerFactory.getLogger(BatchRequestExample.class);

    public static void main(String[] args) throws Exception {
        String address = "b3f7e8e38f8c8393f281a2f0792a2849";

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();

        BatchRequest batchRequest = client.newBatchRequest();

        CompletableFuture<Optional<Account>> r1 = batchRequest.getAccount(address);
        CompletableFuture<Optional<Transaction>> t1 = batchRequest.getAccountTransaction(address, 0, true);
        CompletableFuture<BlockMetadata> m = batchRequest.getMetadata();

        batchRequest.execute();

        logger.info("Account: {}", r1.get().orElse(null));
        logger.info("Transaction: {}", t1.get().orElse(null));
        logger.info("Metadata: {}", m.get());

    }
}
