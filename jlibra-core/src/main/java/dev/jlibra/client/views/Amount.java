package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableAmount.class)
public interface Amount {

    @JsonProperty("amount")
    Long amount();

    @JsonProperty("currency")
    String currency();
}
