package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Amount (
    @JsonProperty("amount") Long amount,
    @JsonProperty("currency") String currency
){}
