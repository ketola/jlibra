package dev.jlibra.client.views.transaction;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableRotateAuthenticationKeyScript.class)
public interface RotateAuthenticationKeyScript extends Script {

}
