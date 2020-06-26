package dev.jlibra.client.views.event;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableNewEpochEventData.class)
public interface NewEpochEventData extends EventData {

    @JsonProperty("epoch")
    Long epoch();
}
