package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutablePeerToPeerTransactionScript.class)
public interface PeerToPeerTransactionScript extends Script {

    @JsonProperty("amount")
    Long amount();

    @JsonProperty("auth_key_prefix")
    String authKeyPrefix();

    @JsonProperty("metadata")
    String metadata();

    @JsonProperty("metadata_signature")
    String metadataSignature();

    @JsonProperty("receiver")
    String receiver();
}
