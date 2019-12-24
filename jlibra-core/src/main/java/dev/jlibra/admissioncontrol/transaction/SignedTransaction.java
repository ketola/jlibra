package dev.jlibra.admissioncontrol.transaction;

import java.security.PublicKey;

import org.immutables.value.Value;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public abstract class SignedTransaction implements LibraSerializable {

    public abstract Transaction getTransaction();

    public abstract PublicKey getPublicKey();

    public abstract Signature getSignature();

    public ByteSequence serialize() {
        return Serializer.builder()
                .appendSerializable(getTransaction())
                .appendPublicKey(getPublicKey())
                .appendSerializable(getSignature())
                .toByteSequence();
    }

    public SubmitTransactionRequest toGrpcObject() {
        types.TransactionOuterClass.SignedTransaction signedTransaction = types.TransactionOuterClass.SignedTransaction
                .newBuilder()
                .setTxnBytes(serialize().toByteString())
                .build();
        return SubmitTransactionRequest.newBuilder()
                .setTransaction(signedTransaction)
                .build();
    }

}
