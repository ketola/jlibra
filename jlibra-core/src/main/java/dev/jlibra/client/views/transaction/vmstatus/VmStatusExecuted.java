package dev.jlibra.client.views.transaction.vmstatus;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableVmStatusExecuted.class)
public interface VmStatusExecuted extends VmStatus {

}
