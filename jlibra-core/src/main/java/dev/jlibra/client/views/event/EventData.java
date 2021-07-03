package dev.jlibra.client.views.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableBaseUrlRotationEventData.class, name = "baseurlrotation"),
        @JsonSubTypes.Type(value = ImmutableBurnEventData.class, name = "burn"),
        @JsonSubTypes.Type(value = ImmutableCancelBurnEventData.class, name = "cancelburn"),
        @JsonSubTypes.Type(value = ImmutableCreateAccountEventData.class, name = "createaccount"),
        @JsonSubTypes.Type(value = ImmutableComplianceKeyRotationEventData.class, name = "compliancekeyrotation"),
        @JsonSubTypes.Type(value = ImmutableMintEventData.class, name = "mint"),
        @JsonSubTypes.Type(value = ImmutablePreBurnEventData.class, name = "preburn"),
        @JsonSubTypes.Type(value = ImmutableSentPaymentEventData.class, name = "sentpayment"),
        @JsonSubTypes.Type(value = ImmutableReceivedPaymentEventData.class, name = "receivedpayment"),
        @JsonSubTypes.Type(value = ImmutableUpgradeEventData.class, name = "upgrade"),
        @JsonSubTypes.Type(value = ImmutableNewEpochEventData.class, name = "epoch"),
        @JsonSubTypes.Type(value = ImmutableNewBlockEventData.class, name = "newblock"),
        @JsonSubTypes.Type(value = ImmutableUnknownEventData.class, name = "unknown")
})
public interface EventData {

}
