package dev.jlibra.client.views;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableTransaction.class)
public interface Transaction {

    @JsonProperty("gas_used")
    Long gasUsed();

    Long version();

    @JsonProperty("vm_status")
    Long vmStatus();

    @JsonProperty("events")
    List<Event> events();

    TransactionData transaction();
}
