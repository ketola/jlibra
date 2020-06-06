package dev.jlibra.client.views.role;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableEmptyAccountRole.class)
public interface EmptyAccountRole extends AccountRole {

}
