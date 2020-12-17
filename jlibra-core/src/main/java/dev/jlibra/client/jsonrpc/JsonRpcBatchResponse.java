package dev.jlibra.client.jsonrpc;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableJsonRpcBatchResponse.class)
public interface JsonRpcBatchResponse {

    @JsonProperty("diem_chain_id")
    long libraChainId();

    @JsonProperty("diem_ledger_version")
    long libraLedgerVersion();

    @JsonProperty("diem_ledger_timestampusec")
    long libraLedgerTimestampusec();

    @JsonProperty("jsonrpc")
    String jsonrpc();

    @JsonProperty("id")
    String id();

    @JsonProperty("result")
    JsonNode result();
}
