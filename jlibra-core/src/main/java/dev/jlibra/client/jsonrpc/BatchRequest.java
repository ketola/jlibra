package dev.jlibra.client.jsonrpc;

import static dev.jlibra.client.jsonrpc.JsonRpcMethod.CURRENCIES_INFO;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_ACCOUNT;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_ACCOUNT_TRANSACTION;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_ACCOUNT_TRANSACTIONS;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_EVENTS;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_METADATA;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_STATE_PROOF;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.GET_TRANSACTIONS;
import static dev.jlibra.client.jsonrpc.JsonRpcMethod.SUBMIT;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.CurrencyInfo;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.event.Event;
import dev.jlibra.client.views.transaction.Transaction;

public class BatchRequest {

    private static final Logger logger = LoggerFactory.getLogger(BatchRequest.class);

    private static final String USER_AGENT = "JLibra";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private HttpClient httpClient;

    private RequestIdGenerator requestIdGenerator;

    private final String url;

    private final ObjectMapper objectMapper;

    Map<JsonRpcRequestInfo, CompletableFuture<?>> requestToResponse = new HashMap<>();

    public BatchRequest(String url, HttpClient client, RequestIdGenerator requestIdGenerator,
            ObjectMapper objectMapper) {
        this.url = url;
        this.objectMapper = objectMapper;
        this.httpClient = client;
        this.requestIdGenerator = requestIdGenerator;
    }

    protected static BatchRequest newBatchRequest(String url, HttpClient client, RequestIdGenerator requestIdGenerator,
            ObjectMapper objectMapper) {
        return new BatchRequest(url, client, requestIdGenerator, objectMapper);
    }

    public CompletableFuture<Optional<Account>> getAccount(String address) {
        CompletableFuture<Optional<Account>> future = new CompletableFuture<>();
        requestToResponse.put(createJsonRpcRequest(Request.create(GET_ACCOUNT, asList(address))), future);
        return future;
    }

    public CompletableFuture<BlockMetadata> getMetadata() {
        CompletableFuture<BlockMetadata> future = new CompletableFuture<>();
        requestToResponse.put(createJsonRpcRequest(Request.create(GET_METADATA, new ArrayList<>())), future);
        return future;
    }

    public CompletableFuture<List<Transaction>> getTransactions(long version, long limit, boolean includeEvents) {
        CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
        requestToResponse.put(
                createJsonRpcRequest(Request.create(GET_TRANSACTIONS, asList(version, limit, includeEvents))),
                future);
        return future;
    }

    public CompletableFuture<List<Transaction>> getAccountTransactions(String address, long start, long limit,
            boolean includeEvents) {
        CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
        requestToResponse.put(createJsonRpcRequest(Request.create(GET_ACCOUNT_TRANSACTIONS,
                asList(address, start, limit, includeEvents))),
                future);
        return future;
    }

    public CompletableFuture<Optional<Transaction>> getAccountTransaction(String address, long sequenceNumber,
            boolean includeEvents) {
        CompletableFuture<Optional<Transaction>> future = new CompletableFuture<>();
        requestToResponse.put(
                createJsonRpcRequest(
                        Request.create(GET_ACCOUNT_TRANSACTION, asList(address, sequenceNumber, includeEvents))),
                future);
        return future;
    }

    public CompletableFuture<List<Event>> getEvents(String eventKey, long start, long limit) {
        CompletableFuture<List<Event>> future = new CompletableFuture<>();
        requestToResponse.put(createJsonRpcRequest(Request.create(GET_EVENTS, asList(eventKey, start, limit))),
                future);
        return future;
    }

    public CompletableFuture<Optional<StateProof>> getStateProof(long knownVersion) {
        CompletableFuture<Optional<StateProof>> future = new CompletableFuture<>();
        requestToResponse.put(createJsonRpcRequest(Request.create(GET_STATE_PROOF, asList(knownVersion))),
                future);
        return future;
    }

    public CompletableFuture<List<CurrencyInfo>> currenciesInfo() {
        CompletableFuture<List<CurrencyInfo>> future = new CompletableFuture<>();
        requestToResponse.put(createJsonRpcRequest(Request.create(CURRENCIES_INFO, new ArrayList<>())),
                future);
        return future;
    }

    public CompletableFuture<Void> submit(String payload) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        requestToResponse.put(createJsonRpcRequest(Request.create(SUBMIT, asList(payload))),
                future);
        return future;
    }

    public void execute() {
        call(requestToResponse.keySet());
    }

    private JsonRpcRequestInfo createJsonRpcRequest(Request request) {
        return ImmutableJsonRpcRequestInfo.builder()
                .method(request.method())
                .request(ImmutableJsonRpcRequest.builder()
                        .id(requestIdGenerator.generateRequestId())
                        .jsonrpc("2.0")
                        .method(request.method().name().toLowerCase())
                        .params(request.params().toArray())
                        .build())
                .build();
    }

    private void call(Set<JsonRpcRequestInfo> requests) {

        String requestJson = convertToJson(requests.stream().map(r -> r.request()).collect(Collectors.toSet()));
        logger.debug("Request: {}", requestJson);

        HttpResponse<String> httpResponse = sendHttpRequest(requestJson);

        String responseBody = httpResponse.body();
        logger.debug("Response: {}", responseBody);

        /**
         * if (isJsonRpcErrorResponse(responseBody)) { try { JsonRpcErrorResponse
         * errorResponse = objectMapper.readValue(responseBody,
         * JsonRpcErrorResponse.class); throw new
         * LibraServerErrorException(errorResponse.error().code(),
         * errorResponse.error().message()); } catch (JsonProcessingException e) { throw
         * new LibraRuntimeException("Converting the response from JSON failed", e); } }
         */

        JavaType responseType = objectMapper.getTypeFactory().constructCollectionType(List.class,
                JsonRpcBatchResponse.class);
        Map<String, JsonRpcMethod> requestIdToMethod = requestToResponse.keySet().stream()
                .collect(toMap(i -> i.request().id(), i -> i.method()));
        Map<String, CompletableFuture> requestIdToFuture = requestToResponse.entrySet().stream()
                .collect(toMap(e -> e.getKey().request().id(), e -> e.getValue()));

        try {
            List<JsonRpcBatchResponse> beanList = (List<JsonRpcBatchResponse>) objectMapper.readValue(responseBody,
                    responseType);

            beanList.forEach(b -> {
                try {
                    JsonRpcMethod method = requestIdToMethod.get(b.id());
                    Object value = objectMapper.treeToValue(b.result(), method.resultType());

                    if (method.isOptional()) {
                        requestIdToFuture.get(b.id())
                                .complete(value == null ? Optional.empty() : Optional.of(value));
                    } else {
                        requestIdToFuture.get(b.id())
                                .complete(value);
                    }

                } catch (JsonProcessingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            // Car car = objectMapper.treeToValue(carJsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return;
    }

    private boolean isJsonRpcErrorResponse(String responseBody) {
        return responseBody.contains("\"error\"");
    }

    private String convertToJson(Set<JsonRpcRequest> jsonRequests) {
        try {
            return objectMapper.writeValueAsString(jsonRequests);
        } catch (JsonProcessingException e) {
            throw new LibraRuntimeException("Converting the request to JSON failed", e);
        }
    }

    private HttpResponse<String> sendHttpRequest(String requestJson) {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", CONTENT_TYPE_JSON)
                .header("User-Agent", USER_AGENT)
                .uri(URI.create(url))
                .POST(BodyPublishers.ofString(requestJson))
                .build();
        try {
            return httpClient.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new LibraRuntimeException("HTTP Request failed", e);
        }
    }
}
