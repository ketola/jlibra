package dev.jlibra.client.views;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CurrencyInfo(
    @JsonProperty("code") String code,
    @JsonProperty("fractional_part") Long fractionalPart,
    @JsonProperty("scaling_factor") Long scalingFactor,
    @JsonProperty("to_xdx_exchange_rate") Float toXdxExchangeRate,
    @JsonProperty("mint_events_key") String mintEventsKey,
    @JsonProperty("burn_events_key") String burnEventsKey,
    @JsonProperty("preburn_events_key") String preburnEventsKey,
    @JsonProperty("cancel_burn_events_key") String cancelBurnEventsKey,
    @JsonProperty("exchange_rate_update_events_key") String exchangeRateUpdateEventsKey
) {
}

