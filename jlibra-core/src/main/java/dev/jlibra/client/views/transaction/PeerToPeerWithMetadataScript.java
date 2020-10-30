package dev.jlibra.client.views.transaction;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutablePeerToPeerWithMetadataScript.class)
public interface PeerToPeerWithMetadataScript extends Script {

    @JsonProperty("amount")
    Long amount();

    @JsonProperty("metadata")
    String metadata();

    @JsonProperty("metadata_signature")
    String metadataSignature();

    @JsonProperty("receiver")
    String receiver();

    @JsonProperty("currency")
    String currency();
}
