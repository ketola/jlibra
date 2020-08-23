package dev.jlibra.client.views.transaction;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.jlibra.client.views.event.Event;
import dev.jlibra.client.views.transaction.vmstatus.VmStatus;

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
    VmStatus vmStatus();

    @JsonProperty("events")
    List<Event> events();

    @JsonProperty("transaction")
    TransactionData transaction();

    @JsonProperty("bytes")
    String bytes();
}
