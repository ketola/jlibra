package dev.jlibra.client.views.role;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableChildVASPAccountRole.class)
public interface ChildVASPAccountRole extends AccountRole {

    @JsonProperty("parent_vasp_address")
    String parentVaspAddress();
}
