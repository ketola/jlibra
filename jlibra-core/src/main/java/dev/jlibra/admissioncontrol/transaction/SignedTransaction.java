package dev.jlibra.admissioncontrol.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public abstract class SignedTransaction implements LibraSerializable {

    public abstract Transaction getTransaction();

    public abstract PublicKey getPublicKey();

    public abstract PrivateKey getPrivateKey();

    public byte[] serialize() {
        return Serializer.builder()
                .appendSerializable(getTransaction())
                .appendPublicKey(getPublicKey())
                .appendByteArray(signTransaction(getTransaction(), getPrivateKey()))
                .toByteArray();
    }

    public SubmitTransactionRequest toGrpcObject() {
        types.TransactionOuterClass.SignedTransaction signedTransaction = types.TransactionOuterClass.SignedTransaction
                .newBuilder()
                .setTxnBytes(ByteString.copyFrom(serialize()))
                .build();
        return SubmitTransactionRequest.newBuilder()
                .setTransaction(signedTransaction)
                .build();
    }

    public byte[] signTransaction(Transaction transaction, PrivateKey privateKey) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] saltDigest = digestSHA3.digest("RawTransaction@@$$LIBRA$$@@".getBytes());
        byte[] transactionBytes = transaction.serialize();
        byte[] saltDigestAndTransaction = new byte[saltDigest.length + transactionBytes.length];

        System.arraycopy(saltDigest, 0, saltDigestAndTransaction, 0, saltDigest.length);
        System.arraycopy(transactionBytes, 0, saltDigestAndTransaction, saltDigest.length, transactionBytes.length);

        byte[] signature;

        try {
            Signature sgr = Signature.getInstance("Ed25519", "BC");
            sgr.initSign(privateKey);
            sgr.update(digestSHA3.digest(saltDigestAndTransaction));
            signature = sgr.sign();
        } catch (Exception e) {
            throw new LibraRuntimeException("Signing the transaction failed", e);
        }

        return signature;
    }
}
