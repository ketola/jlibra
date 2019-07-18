package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

@Value.Immutable
public interface GetAccountState {

    byte[] getAddress();
}
