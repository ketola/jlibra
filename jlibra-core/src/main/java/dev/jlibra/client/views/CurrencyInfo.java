package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableCurrencyInfo.class)
public interface CurrencyInfo {

    @JsonProperty("code")
    String code();

    @JsonProperty("fractional_part")
    Long fractionalPart();

    @JsonProperty("scaling_factor")
    Long scalingFactor();
}
