package dev.jlibra.client.jsonrpc;

import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_ACCOUNT;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_ACCOUNT_TRANSACTION;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_ACCOUNT_TRANSACTIONS;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_CURRENCIES;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_EVENTS;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_METADATA;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_STATE_PROOF;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_TRANSACTIONS;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.SUBMIT;
import static java.util.Arrays.asList;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.jlibra.DiemRuntimeException;
import dev.jlibra.client.DiemServerErrorException;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.CurrencyInfo;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.event.Event;
import dev.jlibra.client.views.transaction.Transaction;

public class DiemJsonRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(DiemJsonRpcClient.class);

    private static final String USER_AGENT = "JLibra";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private HttpClient httpClient;

    private RequestIdGenerator requestIdGenerator;

    private final String url;

    private final ObjectMapper objectMapper;

    public DiemJsonRpcClient(String url, HttpClient client, RequestIdGenerator requestIdGenerator) {
        this.url = url;
        this.objectMapper = ObjectMapperFactory.create();
        this.httpClient = client;
        this.requestIdGenerator = requestIdGenerator;
    }

    public CompletableFuture<Optional<Account>> getAccount(String address) {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_ACCOUNT, asList(address)));
    }

    public CompletableFuture<Optional<BlockMetadata>> getMetadata() {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_METADATA, new ArrayList<>()));
    }

    public CompletableFuture<Optional<List<Transaction>>> getTransactions(long version, long limit,
            boolean includeEvents) {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_TRANSACTIONS,
                asList(version, limit, includeEvents)));
    }

    public CompletableFuture<Optional<List<Transaction>>> getAccountTransactions(String address, long start, long limit,
            boolean includeEvents) {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_ACCOUNT_TRANSACTIONS,
                asList(address, start, limit, includeEvents)));
    }

    public CompletableFuture<Optional<Transaction>> getAccountTransaction(String address, long sequenceNumber,
            boolean includeEvents) {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_ACCOUNT_TRANSACTION,
                asList(address, sequenceNumber, includeEvents)));
    }

    public CompletableFuture<Optional<List<Event>>> getEvents(String eventKey, long start, long limit) {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_EVENTS, asList(eventKey, start, limit)));
    }

    public CompletableFuture<Optional<StateProof>> getStateProof(long knownVersion) {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_STATE_PROOF, asList(knownVersion)));
    }

    public CompletableFuture<Optional<List<CurrencyInfo>>> getCurrencies() {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_CURRENCIES, new ArrayList<>()));
    }

    public BatchRequest newBatchRequest() {
        return BatchRequest.newBatchRequest(url, httpClient, requestIdGenerator, objectMapper);
    }

    public CompletableFuture<Optional<Void>> submit(String payload) {
        return call(Request.create(requestIdGenerator.generateRequestId(), SUBMIT, asList(payload)));
    }

    private <T> CompletableFuture<Optional<T>> call(Request request) {
        JsonRpcRequest jsonRequest = ImmutableJsonRpcRequest.builder()
                .id(request.id())
                .jsonrpc("2.0")
                .method(request.method().name().toLowerCase())
                .params(request.params().toArray())
                .build();

        String requestJson = convertToJson(jsonRequest);
        logger.debug("Request: {}", requestJson);

        return sendHttpRequest(requestJson).handle((httpResponse, ex) -> {
            if (ex != null) {
                throw new DiemRuntimeException("Diem json-rpc call failed", ex);
            }

            String responseBody = httpResponse.body();
            logger.debug("Response: {}", responseBody);

            handleErrorResponse(responseBody);

            JsonRpcResponse<T> response;
            try {
                JavaType type = objectMapper.getTypeFactory().constructParametricType(JsonRpcResponse.class,
                        request.method().resultType());
                response = objectMapper.readValue(responseBody, type);
            } catch (JsonProcessingException e) {
                throw new DiemRuntimeException("Converting the response from JSON failed", e);
            }

            validateRequestAndResponseIds(jsonRequest, response);
            return response.result();
        });
    }

    private void handleErrorResponse(String responseBody) {
        if (isJsonRpcErrorResponse(responseBody)) {
            try {
                JsonRpcErrorResponse errorResponse = objectMapper.readValue(responseBody,
                        JsonRpcErrorResponse.class);
                throw new DiemServerErrorException(errorResponse.error().code(), errorResponse.error().message());
            } catch (JsonProcessingException e) {
                throw new DiemRuntimeException("Converting the response from JSON failed", e);
            }
        }
    }

    private void validateRequestAndResponseIds(JsonRpcRequest jsonRequest, JsonRpcResponse<?> response) {
        if (!jsonRequest.id().equals(response.id())) {
            throw new DiemRuntimeException(String.format(
                    "The json rpc request id (%s) and response id (%s) do not match", jsonRequest.id(),
                    response.id()));
        }
    }

    private boolean isJsonRpcErrorResponse(String responseBody) {
        return responseBody.contains("\"error\"");
    }

    private String convertToJson(JsonRpcRequest jsonRequest) {
        try {
            return objectMapper.writeValueAsString(jsonRequest);
        } catch (JsonProcessingException e) {
            throw new DiemRuntimeException("Converting the request to JSON failed", e);
        }
    }

    private CompletableFuture<HttpResponse<String>> sendHttpRequest(String requestJson) {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", CONTENT_TYPE_JSON)
                .header("User-Agent", USER_AGENT)
                .uri(URI.create(url))
                .POST(BodyPublishers.ofString(requestJson))
                .build();

        return httpClient.sendAsync(request, BodyHandlers.ofString());
    }
}
