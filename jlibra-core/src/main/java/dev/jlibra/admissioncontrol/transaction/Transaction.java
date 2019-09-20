package dev.jlibra.admissioncontrol.transaction;

import static dev.jlibra.serialization.CanonicalSerialization.join;
import static dev.jlibra.serialization.CanonicalSerialization.serializeByteArray;
import static dev.jlibra.serialization.CanonicalSerialization.serializeLong;

import org.immutables.value.Value;

@Value.Immutable
public interface Transaction {

    byte[] getSenderAccount();

    long getSequenceNumber();

    Program getProgram();

    long getExpirationTime();

    long getGasUnitPrice();

    long getMaxGasAmount();

    default byte[] serialize() {
        byte[] result = new byte[0];
        result = join(result, serializeByteArray(getSenderAccount()));
        result = join(result, serializeLong(getSequenceNumber()));
        result = join(result, getProgram().serialize());
        result = join(result, serializeLong(getMaxGasAmount()));
        result = join(result, serializeLong(getGasUnitPrice()));
        result = join(result, serializeLong(getExpirationTime()));
        return result;
    }

}
