package dev.jlibra.example;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.DiemClient;
import dev.jlibra.client.jsonrpc.BatchRequest;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.transaction.Transaction;

/**
 * Diem support the json rpc batch request
 * (https://www.jsonrpc.org/specification#batch) which allows you to send
 * multiple requests to the api in one http request
 */
public class BatchRequestExample {

    private static final Logger logger = LoggerFactory.getLogger(BatchRequestExample.class);

    public static void main(String[] args) throws Exception {
        String address = "79153273a34e0aadf26c963367973760";

        DiemClient client = DiemClient.builder()
                .withUrl("http://localhost:8080")
                .build();

        // 1. Create a new batch request
        BatchRequest batchRequest = client.newBatchRequest();

        // 2. Fill the batch request by calling the methods you want to include in the
        // batch
        CompletableFuture<Optional<Account>> okRequestWithOptional = batchRequest
                .getAccount(AccountAddress.fromHexString(address));
        CompletableFuture<Optional<Account>> failingRequest = batchRequest
                .getAccount(AccountAddress.fromHexString("b3f7e8e38f8c8393f281a2f0792a2849aa"));
        CompletableFuture<Optional<Transaction>> emptyResponse = batchRequest
                .getAccountTransaction(AccountAddress.fromHexString(address), 0, true);
        CompletableFuture<BlockMetadata> okRequest = batchRequest.getMetadata();

        // 3. Execute the batch and the requests will be sent to the api, the method
        // will return after the response has been processed
        batchRequest.execute();

        // 4. Read the responses of the calls
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
