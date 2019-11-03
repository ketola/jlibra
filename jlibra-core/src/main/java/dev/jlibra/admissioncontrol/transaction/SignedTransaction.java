package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
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

    default SubmitTransactionRequest toGrpcObject() {
        types.TransactionOuterClass.SignedTransaction signedTransaction = types.TransactionOuterClass.SignedTransaction
                .newBuilder()
                .setTxnBytes(ByteString.copyFrom(this.serialize()))
                .build();
        return SubmitTransactionRequest.newBuilder()
                .setTransaction(signedTransaction)
                .build();
    }
}
