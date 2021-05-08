package dev.jlibra.client.views;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.jlibra.client.views.role.AccountRole;

@Value.Immutable
@JsonDeserialize(as = ImmutableAccount.class)
public interface Account {

    @JsonProperty("address")
    String address();

    @JsonProperty("authentication_key")
    String authenticationKey();

    @JsonProperty("balances")
    List<Amount> balances();

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

    @JsonProperty("role")
    AccountRole role();

    @JsonProperty("is_frozen")
    Boolean isFrozen();

    @JsonProperty("version")
    Long version();

}
