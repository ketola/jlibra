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
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

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
import java.util.concurrent.ExecutionException;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.jlibra.AccountAddress;
import dev.jlibra.DiemRuntimeException;
import dev.jlibra.client.DiemServerErrorException;
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

    private final HttpClient httpClient;

    private final RequestIdGenerator requestIdGenerator;

    private final String url;

    private final ObjectMapper objectMapper;

    private boolean executed = false;

    private final Map<Request, CompletableFuture> requestToResponse = new HashMap<>();

    private BatchRequest(String url, HttpClient client, RequestIdGenerator requestIdGenerator,
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

    public CompletableFuture<Optional<Account>> getAccount(AccountAddress accountAddress) {
        CompletableFuture<Optional<Account>> future = new CompletableFuture<>();
        requestToResponse.put(
                Request.create(requestIdGenerator.generateRequestId(), GET_ACCOUNT,
                        asList(Hex.toHexString(accountAddress.toArray()))),
                future);
        return future;
    }

    public CompletableFuture<BlockMetadata> getMetadata() {
        CompletableFuture<BlockMetadata> future = new CompletableFuture<>();
        requestToResponse.put(Request.create(requestIdGenerator.generateRequestId(), GET_METADATA, new ArrayList<>()),
                future);
        return future;
    }

    public CompletableFuture<List<Transaction>> getTransactions(long version, long limit, boolean includeEvents) {
        CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
        requestToResponse.put(
                Request.create(requestIdGenerator.generateRequestId(), GET_TRANSACTIONS,
                        asList(version, limit, includeEvents)),
                future);
        return future;
    }

    public CompletableFuture<List<Transaction>> getAccountTransactions(AccountAddress accountAddress, long start,
            long limit,
            boolean includeEvents) {
        CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
        requestToResponse.put(Request.create(requestIdGenerator.generateRequestId(), GET_ACCOUNT_TRANSACTIONS,
                asList(Hex.toHexString(accountAddress.toArray()), start, limit, includeEvents)),
                future);
        return future;
    }

    public CompletableFuture<Optional<Transaction>> getAccountTransaction(AccountAddress accountAddress,
            long sequenceNumber,
            boolean includeEvents) {
        CompletableFuture<Optional<Transaction>> future = new CompletableFuture<>();
        requestToResponse.put(
                Request.create(requestIdGenerator.generateRequestId(), GET_ACCOUNT_TRANSACTION,
                        asList(Hex.toHexString(accountAddress.toArray()), sequenceNumber, includeEvents)),
                future);
        return future;
    }

    public CompletableFuture<List<Event>> getEvents(String eventKey, long start, long limit) {
        CompletableFuture<List<Event>> future = new CompletableFuture<>();
        requestToResponse.put(
                Request.create(requestIdGenerator.generateRequestId(), GET_EVENTS, asList(eventKey, start, limit)),
                future);
        return future;
    }

    public CompletableFuture<Optional<StateProof>> getStateProof(long knownVersion) {
        CompletableFuture<Optional<StateProof>> future = new CompletableFuture<>();
        requestToResponse.put(
                Request.create(requestIdGenerator.generateRequestId(), GET_STATE_PROOF, asList(knownVersion)),
                future);
        return future;
    }

    public CompletableFuture<List<CurrencyInfo>> getCurrencies() {
        CompletableFuture<List<CurrencyInfo>> future = new CompletableFuture<>();
        requestToResponse.put(
                Request.create(requestIdGenerator.generateRequestId(), GET_CURRENCIES, new ArrayList<>()),
                future);
        return future;
    }

    public CompletableFuture<Void> submit(String payload) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        requestToResponse.put(Request.create(requestIdGenerator.generateRequestId(), SUBMIT, asList(payload)),
                future);
        return future;
    }

    public void execute() {
        try {
            executeAsync().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DiemRuntimeException("Batch request failed", e);
        }
    }

    public CompletableFuture<Void> executeAsync() {
        if (this.executed) {
            throw new IllegalStateException("This batch request has already been executed");
        }
        this.executed = true;
        return call(requestToResponse.keySet());
    }

    private JsonRpcRequestInfo createJsonRpcRequest(Request request) {
        return ImmutableJsonRpcRequestInfo.builder()
                .method(request.method())
                .request(ImmutableJsonRpcRequest.builder()
                        .id(request.id())
                        .jsonrpc("2.0")
                        .method(request.method().name().toLowerCase())
                        .params(request.params().toArray())
                        .build())
                .build();
    }

    private CompletableFuture<Void> call(Set<Request> requests) {
        Map<String, Request> requestIdToRequest = requestToResponse.keySet().stream()
                .collect(toMap(i -> i.id(), i -> i));

        String requestJson = convertToJson(
                requests.stream()
                        .map(this::createJsonRpcRequest)
                        .map(JsonRpcRequestInfo::request)
                        .collect(toSet()));

        logger.debug("Request: {}", requestJson);

        return sendHttpRequest(requestJson).handle((httpResponse, ex) -> {
            if (ex != null) {
                throw new DiemRuntimeException("Diem json-rpc batch request failed", ex);
            }

            String responseBody = httpResponse.body();
            logger.debug("Response: {}", responseBody);

            List<JsonNode> responses = deserializeResponse(responseBody);

            for (JsonNode r : responses) {
                String id = r.get("id").asText();
                if (containsError(r)) {
                    JsonRpcErrorResponse errorResponse = deserializeErrorResponse(r);
                    requestToResponse.get(requestIdToRequest.get(id)).completeExceptionally(
                            new DiemServerErrorException(errorResponse.error().code(),
                                    errorResponse.error().message()));
                } else {
                    JsonRpcMethod method = requestIdToRequest.get(id).method();
                    JsonRpcResponse value = deserializeResponseResultObject(r, method);
                    requestToResponse.get(requestIdToRequest.get(id)).complete(value.result());
                }
            }

            return null;
        });
    }

    private boolean containsError(JsonNode r) {
        return r.get("error") != null;
    }

    private JsonRpcErrorResponse deserializeErrorResponse(JsonNode r) {
        try {
            return objectMapper.treeToValue(r, JsonRpcErrorResponse.class);
        } catch (JsonProcessingException e) {
            throw new DiemRuntimeException("json rpc error response deserialization failed", e);
        }
    }

    private JsonRpcResponse deserializeResponseResultObject(JsonNode r, JsonRpcMethod method) {   
    	JavaType type;
    	if(method.isListResult()) {
    		JavaType inner = objectMapper.getTypeFactory().constructParametricType(List.class, method.resultType());
    		type = objectMapper.getTypeFactory().constructParametricType(JsonRpcResponse.class, inner);
    	} else {
    		type = objectMapper.getTypeFactory().constructParametricType(JsonRpcResponse.class,
                    method.resultType());
    	}
        
        try {
			return objectMapper.readValue(r.toString(), type);
		} catch (IOException e) {
			throw new DiemRuntimeException("json rpc response result object deserialization failed", e);
		}
    }

    @SuppressWarnings("unchecked")
    private List<JsonNode> deserializeResponse(String responseBody) {
        JavaType responseType = objectMapper.getTypeFactory().constructCollectionType(List.class,
                JsonNode.class);
        try {
            return (List<JsonNode>) objectMapper.readValue(responseBody,
                    responseType);
        } catch (JsonProcessingException e) {
            throw new DiemRuntimeException("json rpc response deserialization failed", e);
        }
    }

    private String convertToJson(Set<JsonRpcRequest> jsonRequests) {
        try {
            return objectMapper.writeValueAsString(jsonRequests);
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
