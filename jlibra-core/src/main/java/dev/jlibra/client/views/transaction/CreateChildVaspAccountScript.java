package dev.jlibra.client.views.transaction;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableCreateChildVaspAccountScript.class)
public interface CreateChildVaspAccountScript extends Script {

}
