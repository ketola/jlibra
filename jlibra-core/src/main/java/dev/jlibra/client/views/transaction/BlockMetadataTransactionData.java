package dev.jlibra.client.views.transaction;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableBlockMetadataTransactionData.class)
public interface BlockMetadataTransactionData extends TransactionData {

    @JsonProperty("timestamp_usecs")
    Long timestampUsecs();
}
