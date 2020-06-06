package dev.jlibra.client.views.role;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableUnknownAccountRole.class, name = "unknown"),
        @JsonSubTypes.Type(value = ImmutableUnhostedAccountRole.class, name = "unhosted"),
        @JsonSubTypes.Type(value = ImmutableEmptyAccountRole.class, name = "empty"),
        @JsonSubTypes.Type(value = ImmutableChildVASPAccountRole.class, name = "child_vasp"),
        @JsonSubTypes.Type(value = ImmutableParentVASPAccountRole.class, name = "parent_vasp")
})
public interface AccountRole {

}
