package dev.jlibra.client.views.transaction.vmstatus;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableVmStatusMoveAbort.class)
public interface VmStatusMoveAbort extends VmStatus {

    @JsonProperty("location")
    String location();

    @JsonProperty("abort_code")
    Integer abortCode();

    @JsonProperty("explanation")
    MoveAbortExplanation explanation();

}
