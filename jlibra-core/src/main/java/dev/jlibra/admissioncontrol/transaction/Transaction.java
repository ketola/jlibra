package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public interface Transaction extends LibraSerializable {

    byte[] getSenderAccount();

    long getSequenceNumber();

    Program getProgram();

    long getExpirationTime();

    long getGasUnitPrice();

    long getMaxGasAmount();

    default byte[] serialize() {
        return Serializer.builder()
                .appendByteArrayWithoutLengthInformation(getSenderAccount())
                .appendLong(getSequenceNumber())
                .appendSerializable(getProgram())
                .appendLong(getMaxGasAmount())
                .appendLong(getGasUnitPrice())
                .appendLong(getExpirationTime())
                .toByteArray();
    }

}
