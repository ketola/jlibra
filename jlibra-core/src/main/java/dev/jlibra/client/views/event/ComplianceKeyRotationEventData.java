package dev.jlibra.client.views.event;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableComplianceKeyRotationEventData.class)
public interface ComplianceKeyRotationEventData extends EventData {

    @JsonProperty("new_compliance_public_key")
    String newComplianceKey();

    @JsonProperty("time_rotated_seconds")
    Long timeRotatedSeconds();
}
