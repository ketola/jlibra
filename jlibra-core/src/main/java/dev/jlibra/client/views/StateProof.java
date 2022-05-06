package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record StateProof(
    @JsonProperty("ledger_consistency_proof") String ledgerConsistencyProof,
    @JsonProperty("ledger_info_with_signatures") String ledgerInfoWithSignatures,
    @JsonProperty("epoch_change_proof") String epochChangeProof
){}
