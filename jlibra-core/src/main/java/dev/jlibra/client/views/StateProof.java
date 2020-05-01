package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableStateProof.class)
public interface StateProof {

    @JsonProperty("ledger_consistency_proof")
    String ledgetConsistencyProof();

    @JsonProperty("ledger_info_with_signatures")
    String ledgerInfoWithSignatures();

    @JsonProperty("validator_change_proof")
    String validatorChangeProof();

}
