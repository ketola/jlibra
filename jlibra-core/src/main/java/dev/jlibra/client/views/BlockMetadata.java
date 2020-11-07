package dev.jlibra.client.views;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableBlockMetadata.class)
public interface BlockMetadata {

    @JsonProperty("timestamp")
    Long timestamp();

    @JsonProperty("version")
    Long version();

    @JsonProperty("accumulator_root_hash")
    String accumulatorRootHash();

    @JsonProperty("chain_id")
    Long chainId();

    @JsonProperty("libra_version")
    Long libraVersion();

    @JsonProperty("module_publishing_allowed")
    boolean modulePublishingAllowed();

    @JsonProperty("script_hash_allow_list")
    List<String> scriptHashAllowList();

}
