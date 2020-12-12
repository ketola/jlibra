package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.dcs.DCS;

@Value.Immutable
@DCS.Structure
public interface Transaction {

    @DCS.Field(value = 0, fixedLength = true)
    AccountAddress getSender();

    @DCS.Field(1)
    long getSequenceNumber();

    @DCS.Field(2)
    TransactionPayload getPayload();

    @DCS.Field(3)
    long getMaxGasAmount();

    @DCS.Field(4)
    long getGasUnitPrice();

    @DCS.Field(5)
    String getGasCurrencyCode();

    @DCS.Field(6)
    long getExpirationTimestampSecs();

    @DCS.Field(7)
    ChainId chainId();

}
