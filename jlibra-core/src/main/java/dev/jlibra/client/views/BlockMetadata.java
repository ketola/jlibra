package dev.jlibra.client.views;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record BlockMetadata(
    @JsonProperty("timestamp") Long timestamp,
    @JsonProperty("version") Long version,
    @JsonProperty("accumulator_root_hash") String accumulatorRootHash,
    @JsonProperty("chain_id") Long chainId,
    @JsonProperty("diem_version") Long libraVersion,
    @JsonProperty("module_publishing_allowed") boolean modulePublishingAllowed,
    @JsonProperty("script_hash_allow_list") List<String> scriptHashAllowList,
    @JsonProperty("dual_attestation_limit") Long dualAttestationLimit
){}
