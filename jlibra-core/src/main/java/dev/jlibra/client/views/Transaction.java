package dev.jlibra.client.views;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.jlibra.client.views.event.Event;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableTransaction.class)
public interface Transaction {

    @JsonProperty("gas_used")
    Long gasUsed();

    @JsonProperty("hash")
    String hash();

    @JsonProperty("version")
    Long version();

    @JsonProperty("vm_status")
    String vmStatus();

    @JsonProperty("events")
    List<Event> events();

    @JsonProperty("transaction")
    TransactionData transaction();
}
