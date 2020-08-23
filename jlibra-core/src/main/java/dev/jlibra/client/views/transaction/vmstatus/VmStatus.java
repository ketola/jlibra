package dev.jlibra.client.views.transaction.vmstatus;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImmutableVmStatusExecuted.class, name = "executed"),
        @JsonSubTypes.Type(value = ImmutableVmStatusOutOfGas.class, name = "out_of_gas"),
        @JsonSubTypes.Type(value = ImmutableVmStatusMoveAbort.class, name = "move_abort"),
        @JsonSubTypes.Type(value = ImmutableVmStatusExecutionFailure.class, name = "execution_failure"),
        @JsonSubTypes.Type(value = ImmutableVmStatusVerificationError.class, name = "verification_error"),
        @JsonSubTypes.Type(value = ImmutableVmStatusDeserializationError.class, name = "deserialization_error"),
        @JsonSubTypes.Type(value = ImmutableVmStatusPublishingFailure.class, name = "publishing_failure"),
})
public interface VmStatus {
}
