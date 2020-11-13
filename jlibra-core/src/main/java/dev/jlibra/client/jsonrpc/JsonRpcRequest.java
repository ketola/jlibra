package dev.jlibra.client.jsonrpc;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableJsonRpcRequest.class)
public interface JsonRpcRequest {

    String jsonrpc();

    String method();

    Object[] params();

    String id();

    public static JsonRpcRequest create(String requestId, String method, Object[] params) {
        return ImmutableJsonRpcRequest.builder()
                .id(requestId)
                .jsonrpc("2.0")
                .method(method)
                .params(params)
                .build();
    }
}
