package dev.jlibra.client.views.event;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableUnknownEventData.class)
public interface UnknownEventData extends EventData {

}
