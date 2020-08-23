package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.jlibra.client.views.transaction.TransactionData;

@Value.Immutable
@JsonDeserialize(as = ImmutableBlockMetadataTransaction.class)
public interface BlockMetadataTransaction extends TransactionData {

    @JsonProperty("timestamp_usecs")
    Long timestampUsecs();
}
