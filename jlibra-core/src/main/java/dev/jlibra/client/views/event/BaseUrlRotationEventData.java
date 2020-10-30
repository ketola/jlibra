package dev.jlibra.client.views.event;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableBaseUrlRotationEventData.class)
public interface BaseUrlRotationEventData extends EventData {

    @JsonProperty("new_base_url")
    String newBaseUrl();

    @JsonProperty("time_rotated_seconds")
    Long timeRotatedSeconds();
}
