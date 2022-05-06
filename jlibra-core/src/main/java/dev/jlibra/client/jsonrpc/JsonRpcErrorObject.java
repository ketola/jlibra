package dev.jlibra.client.jsonrpc;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record JsonRpcErrorObject (
    @JsonProperty("code") Integer code,
    @JsonProperty("message") String message,
    @JsonProperty("data") JsonNode data
){}
