package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableEvent.class)
public interface Event {

    String key();

    @JsonProperty("sequence_number")
    Long sequenceNumber();

    @JsonProperty("transaction_version")
    Long transactionVersion();

    EventData data();

}
