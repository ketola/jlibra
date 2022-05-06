package dev.jlibra.client.jsonrpc;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record JsonRpcErrorResponse (
    @JsonProperty("diem_chain_id") Long libraChainId,
    @JsonProperty("diem_ledger_version") Long libraLedgerVersion,
    @JsonProperty("diem_ledger_timestampusec") Long libraLedgerTimestampusec,
    @JsonProperty("jsonrpc") String jsonrpc,
    @JsonProperty("id") String id,
    @JsonProperty("error") JsonRpcErrorObject error
){}
