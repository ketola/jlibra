package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public interface Transaction extends LibraSerializable {

    AccountAddress getSenderAccount();

    long getSequenceNumber();

    Script getPayload();

    long getExpirationTime();

    long getGasUnitPrice();

    long getMaxGasAmount();

    default byte[] serialize() {
        return Serializer.builder()
                .appendByteArrayWithoutLengthInformation(getSenderAccount().asByteArray())
                .appendLong(getSequenceNumber())
                .appendSerializable(getPayload())
                .appendLong(getMaxGasAmount())
                .appendLong(getGasUnitPrice())
                .appendLong(getExpirationTime())
                .toByteArray();
    }

}
