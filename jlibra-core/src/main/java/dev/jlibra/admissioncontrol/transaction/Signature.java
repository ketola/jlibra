package dev.jlibra.admissioncontrol.transaction;

import java.security.PrivateKey;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.immutables.value.Value;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public abstract class Signature implements LibraSerializable {

    public abstract Transaction getTransaction();

    public abstract PrivateKey getPrivateKey();

    @Override
    public byte[] serialize() {
        return Serializer.builder()
                .appendByteArray(signTransaction(getTransaction(), getPrivateKey()))
                .toByteArray();
    }

    protected byte[] signTransaction(Transaction transaction, PrivateKey privateKey) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] saltDigest = digestSHA3.digest("RawTransaction::libra_types::transaction@@$$LIBRA$$@@".getBytes());
        byte[] transactionBytes = transaction.serialize();
        byte[] saltDigestAndTransaction = new byte[saltDigest.length + transactionBytes.length];

        System.arraycopy(saltDigest, 0, saltDigestAndTransaction, 0, saltDigest.length);
        System.arraycopy(transactionBytes, 0, saltDigestAndTransaction, saltDigest.length, transactionBytes.length);

        byte[] signature;

        try {
            java.security.Signature sgr = java.security.Signature.getInstance("Ed25519", "BC");
            sgr.initSign(privateKey);
            sgr.update(digestSHA3.digest(saltDigestAndTransaction));
            signature = sgr.sign();
        } catch (Exception e) {
            throw new LibraRuntimeException("Signing the transaction failed", e);
        }

        return signature;
    }

}
