package dev.jlibra.client.views.event;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableUpgradeEventData.class)
public interface UpgradeEventData extends EventData {

    @JsonProperty("write_set")
    String writeSet();
}
