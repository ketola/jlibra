package dev.jlibra.client.jsonrpc;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableJsonRpcErrorResponse.class)
public interface JsonRpcErrorResponse<T> {

}
