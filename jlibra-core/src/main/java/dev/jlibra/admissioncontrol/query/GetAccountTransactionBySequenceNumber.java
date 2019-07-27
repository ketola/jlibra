package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

@Value.Immutable
public interface GetAccountTransactionBySequenceNumber {

    byte[] getAccountAddress();

    long getSequenceNumber();

}
