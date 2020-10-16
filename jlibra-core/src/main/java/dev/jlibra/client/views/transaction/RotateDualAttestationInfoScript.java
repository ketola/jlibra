package dev.jlibra.client.views.transaction;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableRotateDualAttestationInfoScript.class)
public interface RotateDualAttestationInfoScript extends Script {

}
