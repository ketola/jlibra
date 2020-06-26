package dev.jlibra.client.views.event;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableNewBlockEventData.class)
public interface NewBlockEventData extends EventData {

    @JsonProperty("round")
    Long round();

    @JsonProperty("proposer")
    String proposer();

    @JsonProperty("proposed_time")
    Long proposedTime();
}
