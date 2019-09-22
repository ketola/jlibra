package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public interface SignedTransaction extends LibraSerializable {

    Transaction getTransaction();

    byte[] getPublicKey();

    byte[] getSignature();

    default byte[] serialize() {
        return Serializer.builder()
                .appendSerializable(getTransaction())
                .appendByteArray(getPublicKey())
                .appendByteArray(getSignature())
                .toByteArray();
    }
}
