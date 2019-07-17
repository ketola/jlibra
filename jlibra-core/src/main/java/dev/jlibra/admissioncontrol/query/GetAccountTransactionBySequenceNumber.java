package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

@Value.Immutable
public interface GetAccountTransactionBySequenceNumber {

    public byte[] getAccountAddress();

    public long getSequenceNumber();

}
