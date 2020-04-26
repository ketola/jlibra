package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableReceivedPaymentEventData.class)
public interface ReceivedPaymentEventData extends EventData {

    @JsonProperty("amount")
    Amount amount();

    @JsonProperty("metadata")
    String metadata();

    @JsonProperty("sender")
    String sender();
}
