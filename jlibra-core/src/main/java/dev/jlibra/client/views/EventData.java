package dev.jlibra.client.views;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableUnknownEventData.class, name = "unknown"),
        @JsonSubTypes.Type(value = ImmutableSentPaymentEventData.class, name = "sentpayment"),
        @JsonSubTypes.Type(value = ImmutableReceivedPaymentEventData.class, name = "receivedpayment")
})
public interface EventData {

}
