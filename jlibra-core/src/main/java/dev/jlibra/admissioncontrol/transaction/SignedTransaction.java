package dev.jlibra.admissioncontrol.transaction;

import static dev.jlibra.serialization.CanonicalSerialization.join;
import static dev.jlibra.serialization.CanonicalSerialization.serializeByteArray;

import org.immutables.value.Value;

@Value.Immutable
public interface SignedTransaction {

    Transaction getTransaction();

    byte[] getPublicKey();

    byte[] getSignature();

    default byte[] serialize() {
        byte[] result = getTransaction().serialize();
        result = join(result, serializeByteArray(getPublicKey()));
        result = join(result, serializeByteArray(getSignature()));
        return result;
    }
}
