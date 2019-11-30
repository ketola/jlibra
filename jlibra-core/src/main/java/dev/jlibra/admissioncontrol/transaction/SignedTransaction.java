package dev.jlibra.admissioncontrol.transaction;

import java.security.PublicKey;

import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public abstract class SignedTransaction implements LibraSerializable {

    public abstract Transaction getTransaction();

    public abstract PublicKey getPublicKey();

    public abstract byte[] getSignature();

    public byte[] serialize() {
        return Serializer.builder()
                .appendSerializable(getTransaction())
                .appendPublicKey(getPublicKey())
                .appendByteArray(getSignature())
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
}
