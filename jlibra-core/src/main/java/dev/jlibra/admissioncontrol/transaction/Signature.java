package dev.jlibra.admissioncontrol.transaction;

import java.security.PrivateKey;

import org.immutables.value.Value;

import dev.jlibra.Hash;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;
import dev.jlibra.serialization.lcs.LCSSerializer;

@Value.Immutable
@LCS.Structure
public abstract class Signature {

    @LCS.Field(0)
    public abstract ByteSequence getSignature();

    public static Signature signTransaction(Transaction transaction, PrivateKey privateKey) {
        ByteSequence transactionBytes = new LCSSerializer().serialize(transaction, Transaction.class);

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

        return ImmutableSignature.builder()
                .signature(ByteSequence.from(signature))
                .build();
    }

}
