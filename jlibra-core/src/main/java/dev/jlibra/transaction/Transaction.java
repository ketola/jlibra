package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
@LCS.Structure
public interface Transaction {

    @LCS.Field(value = 0, fixedLength = true)
    AccountAddress getSenderAccount();

    @LCS.Field(1)
    long getSequenceNumber();

    @LCS.Field(2)
    TransactionPayload getPayload();

    @LCS.Field(3)
    long getMaxGasAmount();

    @LCS.Field(4)
    long getGasUnitPrice();

    @LCS.Field(5)
    String getGasCurrencyCode();

    @LCS.Field(6)
    long getExpirationTime();

}
