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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.client.LibraServerErrorException;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.BlockMetadata;
import dev.jlibra.client.views.CurrencyInfo;
import dev.jlibra.client.views.StateProof;
import dev.jlibra.client.views.event.Event;
import dev.jlibra.client.views.transaction.Transaction;

public class LibraJsonRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(LibraJsonRpcClient.class);

    private static final String USER_AGENT = "JLibra";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private HttpClient httpClient;

    private RequestIdGenerator requestIdGenerator;

    private final String url;

    private final ObjectMapper objectMapper;

    public LibraJsonRpcClient(String url, HttpClient client, RequestIdGenerator requestIdGenerator) {
        this.url = url;
        this.objectMapper = ObjectMapperFactory.create();
        this.httpClient = client;
        this.requestIdGenerator = requestIdGenerator;
    }

    public Optional<Account> getAccount(String address) {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_ACCOUNT, asList(address)));
    }

    public BlockMetadata getMetadata() {
        return (BlockMetadata) call(
                Request.create(requestIdGenerator.generateRequestId(), GET_METADATA, new ArrayList<>())).get();
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getTransactions(long version, long limit, boolean includeEvents) {
        return (List<Transaction>) call(Request.create(requestIdGenerator.generateRequestId(), GET_TRANSACTIONS,
                asList(version, limit, includeEvents)))
                        .get();
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAccountTransactions(String address, long start, long limit, boolean includeEvents) {
        return (List<Transaction>) call(Request.create(requestIdGenerator.generateRequestId(), GET_ACCOUNT_TRANSACTIONS,
                asList(address, start, limit, includeEvents)))
                        .get();
    }

    public Optional<Transaction> getAccountTransaction(String address, long sequenceNumber, boolean includeEvents) {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_ACCOUNT_TRANSACTION,
                asList(address, sequenceNumber, includeEvents)));
    }

    @SuppressWarnings("unchecked")
    public List<Event> getEvents(String eventKey, long start, long limit) {
        return (List<Event>) call(
                Request.create(requestIdGenerator.generateRequestId(), GET_EVENTS, asList(eventKey, start, limit)))
                        .get();
    }

    public Optional<StateProof> getStateProof(long knownVersion) {
        return call(Request.create(requestIdGenerator.generateRequestId(), GET_STATE_PROOF, asList(knownVersion)));
    }

    @SuppressWarnings("unchecked")
    public List<CurrencyInfo> currenciesInfo() {
        return (List<CurrencyInfo>) call(
                Request.create(requestIdGenerator.generateRequestId(), CURRENCIES_INFO, new ArrayList<>())).get();
    }

    public BatchRequest newBatchRequest() {
        return BatchRequest.newBatchRequest(url, httpClient, requestIdGenerator, objectMapper);
    }

    public void submit(String payload) {
        call(Request.create(requestIdGenerator.generateRequestId(), SUBMIT, asList(payload)));
    }

    private <T> Optional<T> call(Request request) {
        JsonRpcRequest jsonRequest = ImmutableJsonRpcRequest.builder()
                .id(request.id())
                .jsonrpc("2.0")
                .method(request.method().name().toLowerCase())
                .params(request.params().toArray())
                .build();

        String requestJson = convertToJson(jsonRequest);
        logger.debug("Request: {}", requestJson);

        HttpResponse<String> httpResponse = sendHttpRequest(requestJson);

        String responseBody = httpResponse.body();
        logger.debug("Response: {}", responseBody);

        if (isJsonRpcErrorResponse(responseBody)) {
            try {
                JsonRpcErrorResponse errorResponse = objectMapper.readValue(responseBody, JsonRpcErrorResponse.class);
                throw new LibraServerErrorException(errorResponse.error().code(), errorResponse.error().message());
            } catch (JsonProcessingException e) {
                throw new LibraRuntimeException("Converting the response from JSON failed", e);
            }
        }

        JsonRpcResponse<T> response;
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametricType(JsonRpcResponse.class,
                    request.method().resultType());
            response = objectMapper.readValue(responseBody, type);
        } catch (JsonProcessingException e) {
            throw new LibraRuntimeException("Converting the response from JSON failed", e);
        }

        if (!jsonRequest.id().equals(response.id())) {
            throw new LibraRuntimeException(String.format(
                    "The json rpc request id (%s) and response id (%s) do not match", jsonRequest.id(), response.id()));
        }
        return response.result();
    }

    private boolean isJsonRpcErrorResponse(String responseBody) {
        return responseBody.contains("\"error\"");
    }

    private String convertToJson(JsonRpcRequest jsonRequest) {
        try {
            return objectMapper.writeValueAsString(jsonRequest);
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
