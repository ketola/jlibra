package dev.jlibra.client.jsonrpc;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dev.jlibra.client.DiemServerErrorException;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.transaction.Transaction;

public class BatchRequestTest {

    private static final String URL = "http://libra-endpoint";

    private HttpClient httpClient;

    private HttpResponse<Object> httpResponse;

    private RequestIdGenerator requestIdGenerator;

    @BeforeEach
    public void setUp() throws Exception {
        this.httpClient = mock(HttpClient.class);
        this.httpResponse = mock(HttpResponse.class);
        this.requestIdGenerator = mock(RequestIdGenerator.class);
        when(httpClient.send(Mockito.any(), Mockito.any())).thenReturn(httpResponse);
    }

    @Test
    public void testBatchRequest() throws Exception {
        when(requestIdGenerator.generateRequestId()).thenReturn("abe36913-051c-4d81-856d-f0fa9b5f0c42",
                "db88b755-f890-4afb-a13f-c326adae4751", "f831dd4f-6e74-4967-83b6-edb4c058ffe5");
        when(httpResponse.body())
                .thenReturn(IOUtils.toString(BatchRequestTest.class.getResourceAsStream("/batch_response_ok.json"),
                        UTF_8));

        BatchRequest batchRequest = BatchRequest.newBatchRequest(URL, httpClient, requestIdGenerator,
                ObjectMapperFactory.create());

        CompletableFuture<List<Transaction>> accountTransactions = batchRequest
                .getAccountTransactions("6bb714ccebc343c0bf3e6ff7f5f73001", 0, 5, true);
        CompletableFuture<BlockMetadata> blockmetadata = batchRequest.getMetadata();
        CompletableFuture<Optional<Account>> account = batchRequest.getAccount("b3f7e8e38f8c8393f281a2f0792a2849");

        assertFalse(accountTransactions.isDone());
        assertFalse(blockmetadata.isDone());
        assertFalse(account.isDone());

        batchRequest.execute();

        assertTrue(accountTransactions.isDone());
        assertTrue(blockmetadata.isDone());
        assertTrue(account.isDone());

        assertNotNull(accountTransactions.get());
        assertNotNull(blockmetadata.get());
        assertNotNull(account.get().orElse(null));
    }

    @Test
    public void testBatchRequestError() throws Exception {
        when(requestIdGenerator.generateRequestId()).thenReturn("ff8854c1-2983-4c9c-b3a2-8391a4719f7e");
        when(httpResponse.body())
                .thenReturn(IOUtils.toString(BatchRequestTest.class.getResourceAsStream("/batch_response_error.json"),
                        UTF_8));

        BatchRequest batchRequest = BatchRequest.newBatchRequest(URL, httpClient, requestIdGenerator,
                ObjectMapperFactory.create());

        CompletableFuture<Optional<Account>> account = batchRequest.getAccount("b3f7e8e38f8c8393f281a2f0792a2849aa");

        batchRequest.execute();

        assertTrue(account.isDone());

        assertTrue(account.isCompletedExceptionally());

        ExecutionException ex = assertThrows(ExecutionException.class, () -> {
            account.get().orElse(null);
        });

        assertEquals(-32602, ((DiemServerErrorException) ex.getCause()).getCode());
        assertEquals("Invalid param account address(params[0]): should be hex-encoded string",
                ((DiemServerErrorException) ex.getCause()).getErrorMessage());
    }

    @Test
    public void testBatchRequestEmptyOptional() throws Exception {
        when(requestIdGenerator.generateRequestId()).thenReturn("b6d85560-0d16-41b9-bf26-c5d14c2f1075");
        when(httpResponse.body())
                .thenReturn(IOUtils.toString(
                        BatchRequestTest.class.getResourceAsStream("/batch_response_empty_optional.json"),
                        UTF_8));

        BatchRequest batchRequest = BatchRequest.newBatchRequest(URL, httpClient, requestIdGenerator,
                ObjectMapperFactory.create());

        CompletableFuture<Optional<Account>> account = batchRequest.getAccount("b3f7e8e38f8c8393f281a2f0792a28aa");

        batchRequest.execute();

        assertTrue(account.isDone());
        assertFalse(account.isCompletedExceptionally());
        assertTrue(account.get().isEmpty());
    }
}
