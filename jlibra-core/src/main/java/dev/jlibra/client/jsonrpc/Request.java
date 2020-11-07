package dev.jlibra.client.jsonrpc;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public interface Request {

    JsonRpcMethod method();

    List<Object> params();

    public static Request create(JsonRpcMethod method, List<Object> params) {
        return ImmutableRequest.builder()
                .method(method)
                .params(params)
                .build();
    }

}
