package dev.jlibra.client.views.role;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableUnknownAccountRole.class)
public interface UnknownAccountRole extends AccountRole {

}
