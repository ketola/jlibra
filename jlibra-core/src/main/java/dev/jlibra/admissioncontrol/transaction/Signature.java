package dev.jlibra.admissioncontrol.transaction;

import java.security.PrivateKey;

import org.immutables.value.Value;

import dev.jlibra.Hash;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public abstract class Signature implements LibraSerializable {

    public abstract Transaction getTransaction();

    public abstract PrivateKey getPrivateKey();

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .append(signTransaction(getTransaction(), getPrivateKey()))
                .toByteSequence();
    }

    protected ByteSequence signTransaction(Transaction transaction, PrivateKey privateKey) {
        ByteSequence transactionBytes = transaction.serialize();

        byte[] signature;

        try {
            java.security.Signature sgr = java.security.Signature.getInstance("Ed25519", "BC");
            sgr.initSign(privateKey);
            sgr.update(Hash.ofInput(transactionBytes)
                    .hash(ByteSequence.from("RawTransaction::libra_types::transaction@@$$LIBRA$$@@".getBytes()))
                    .toArray());
            signature = sgr.sign();
        } catch (Exception e) {
            throw new LibraRuntimeException("Signing the transaction failed", e);
        }

        return ByteSequence.from(signature);
    }

}
