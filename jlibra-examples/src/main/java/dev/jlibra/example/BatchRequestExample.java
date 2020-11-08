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

        CompletableFuture<Optional<Account>> okRequestWithOptional = batchRequest.getAccount(address);
        CompletableFuture<Optional<Account>> failingRequest = batchRequest
                .getAccount("b3f7e8e38f8c8393f281a2f0792a2849aa");
        CompletableFuture<Optional<Transaction>> emptyResponse = batchRequest.getAccountTransaction(address, 0, true);
        CompletableFuture<BlockMetadata> okRequest = batchRequest.getMetadata();

        batchRequest.execute();

        logger.info("Account: {}", okRequestWithOptional.get().orElse(null));
        logger.info("Transaction: {}", emptyResponse.get().orElse(null));
        logger.info("Metadata: {}", okRequest.get());

        try {
            logger.info("Account: {}", failingRequest.get().orElse(null));
        } catch (Exception e) {
            logger.info("Request failed: " + e.getMessage());
        }
    }

}
