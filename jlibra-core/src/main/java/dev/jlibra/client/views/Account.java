package dev.jlibra.client.views;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableAccount.class)
public interface Account {

    @JsonProperty("authentication_key")
    String authenticationKey();

    @JsonProperty("balance")
    Amount balance();

    @JsonProperty("delegated_key_rotation_capability")
    Boolean delegatedKeyRotationCapability();

    @JsonProperty("delegated_withdrawal_capability")
    Boolean delegatedWithdrawalCapability();

    @JsonProperty("received_events_key")
    String receivedEventsKey();

    @JsonProperty("sent_events_key")
    String sentEventsKey();

    @JsonProperty("sequence_number")
    Long sequenceNumber();

}
