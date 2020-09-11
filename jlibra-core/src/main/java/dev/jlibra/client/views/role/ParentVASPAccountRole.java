package dev.jlibra.client.views.role;

import java.math.BigInteger;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableParentVASPAccountRole.class)
public interface ParentVASPAccountRole extends AccountRole {

    @JsonProperty("human_name")
    String humanName();

    @JsonProperty("base_url")
    String baseUrl();

    @JsonProperty("expiration_time")
    BigInteger expirationTime();

    @JsonProperty("compliance_key")
    String complianceKey();

    @JsonProperty("num_children")
    Long numChildren();

    @JsonProperty("base_url_rotation_events_key")
    String baseUrlRotationEventsKey();

    @JsonProperty("compliance_key_rotation_events_key")
    String complianceKeyRotationEventsKey();
}
