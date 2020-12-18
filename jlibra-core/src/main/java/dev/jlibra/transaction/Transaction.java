package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.bcs.BCS;

@Value.Immutable
@BCS.Structure
public interface Transaction {

    @BCS.Field(value = 0, fixedLength = true)
    AccountAddress getSender();

    @BCS.Field(1)
    long getSequenceNumber();

    @BCS.Field(2)
    TransactionPayload getPayload();

    @BCS.Field(3)
    long getMaxGasAmount();

    @BCS.Field(4)
    long getGasUnitPrice();

    @BCS.Field(5)
    String getGasCurrencyCode();

    @BCS.Field(6)
    long getExpirationTimestampSecs();

    @BCS.Field(7)
    ChainId chainId();

}
