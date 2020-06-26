package dev.jlibra.client.views.event;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.jlibra.client.views.Amount;

@Value.Immutable
@JsonDeserialize(as = ImmutableSentPaymentEventData.class)
public interface SentPaymentEventData extends EventData {

    @JsonProperty("amount")
    Amount amount();

    @JsonProperty("metadata")
    String metadata();

    @JsonProperty("receiver")
    String receiver();

}
