package dev.jlibra.client.jsonrpc;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableJsonRpcErrorResponse.class)
public interface JsonRpcErrorResponse {

    @JsonProperty("libra_chain_id")
    Long libraChainId();

    @JsonProperty("libra_ledger_version")
    Long libraLedgerVersion();

    @JsonProperty("libra_ledger_timestampusec")
    Long libraLedgerTimestampusec();

    @JsonProperty("jsonrpc")
    String jsonrpc();

    @JsonProperty("id")
    String id();

    @JsonProperty("error")
    JsonRpcErrorObject error();
}
