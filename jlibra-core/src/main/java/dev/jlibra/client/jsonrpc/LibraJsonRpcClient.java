package dev.jlibra.client.jsonrpc;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

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

    private static final Object[] EMTPY_PARAMS = new Object[0];

    private static final String USER_AGENT = "JLibra";

    private static final String CONTENT_TYPE_JSON = "application/json";

    private HttpClient httpClient;

    private final String url;

    private final ObjectMapper objectMapper;

    public LibraJsonRpcClient(String url, HttpClient client) {
        this.url = url;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new Jdk8Module());
        this.httpClient = client;
    }

    public Optional<Account> getAccount(String address) {
        return request("get_account", new Object[] { address }, Account.class);
    }

    public BlockMetadata getMetadata() {
        return request("get_metadata", EMTPY_PARAMS, BlockMetadata.class).get();
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getTransactions(long version, long limit, boolean includeEvents) {
        return request("get_transactions", new Object[] { version, limit, includeEvents }, List.class).get();
    }

    @SuppressWarnings("unchecked")
    public List<Transaction> getAccountTransactions(String address, long start, long limit, boolean includeEvents) {
        return request("get_account_transactions", new Object[] { address, start, limit, includeEvents }, List.class)
                .get();
    }

    public Optional<Transaction> getAccountTransaction(String address, long sequenceNumber, boolean includeEvents) {
        return request("get_account_transaction", new Object[] { address, sequenceNumber, includeEvents },
                Transaction.class);
    }

    @SuppressWarnings("unchecked")
    public List<Event> getEvents(String eventKey, long start, long limit) {
        return request("get_events", new Object[] { eventKey, start, limit }, List.class).get();
    }

    public Optional<StateProof> getStateProof(long knownVersion) {
        return request("get_state_proof", new Object[] { knownVersion }, StateProof.class);
    }

    @SuppressWarnings("unchecked")
    public List<CurrencyInfo> currenciesInfo() {
        return request("currencies_info", EMTPY_PARAMS, List.class).get();
    }

    public void submit(String payload) {
        request("submit", new Object[] { payload }, Void.class);
    }

    public <T> Optional<T> request(String method, Object[] params, Class<T> resultType) {
        JsonRpcRequest jsonRequest = ImmutableJsonRpcRequest.builder()
                .id(UUID.randomUUID().toString())
                .jsonrpc("2.0")
                .method(method)
                .params(params)
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
            JavaType type = objectMapper.getTypeFactory().constructParametricType(JsonRpcResponse.class, resultType);
            response = objectMapper.readValue(responseBody, type);
        } catch (JsonProcessingException e) {
            throw new LibraRuntimeException("Converting the response from JSON failed", e);
        }

        // TODO: verify request / response ids

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
