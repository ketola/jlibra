package dev.jlibra.client.jsonrpc;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface Request {

    String id();

    JsonRpcMethod method();

    List<Object> params();

    public static Request create(String id, JsonRpcMethod method, List<Object> params) {
        return ImmutableRequest.builder()
                .id(id)
                .method(method)
                .params(params)
                .build();
    }

}
