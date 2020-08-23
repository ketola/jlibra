package dev.jlibra.client.views.transaction.vmstatus;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableVmStatusVerificationError.class)
public interface VmStatusVerificationError extends VmStatus {

}
