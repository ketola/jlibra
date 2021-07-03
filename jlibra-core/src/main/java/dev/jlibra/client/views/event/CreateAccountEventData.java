package dev.jlibra.client.views.event;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableCreateAccountEventData.class)
public interface CreateAccountEventData extends EventData {

    @JsonProperty("created_address")
    String createdAddress();

    @JsonProperty("role_id")
    Integer roleId();

}
