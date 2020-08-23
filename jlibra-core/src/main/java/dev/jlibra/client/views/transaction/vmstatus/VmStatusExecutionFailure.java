package dev.jlibra.client.views.transaction.vmstatus;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableVmStatusExecutionFailure.class)
public interface VmStatusExecutionFailure extends VmStatus {

    @JsonProperty("location")
    String location();

    @JsonProperty("function_index")
    Integer functionIndex();

    @JsonProperty("code_offset")
    Integer codeOffset();
}
