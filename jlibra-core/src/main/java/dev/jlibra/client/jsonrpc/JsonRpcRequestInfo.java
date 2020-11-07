package dev.jlibra.client.jsonrpc;

import org.immutables.value.Value;

@Value.Immutable
public interface JsonRpcRequestInfo {

    JsonRpcRequest request();

    JsonRpcMethod method();
}
