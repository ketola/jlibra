package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import dev.jlibra.serialization.lcs.LCS;
import dev.jlibra.serialization.lcs.LCSSerializer;

@LCS.Structure
@Value.Immutable
public abstract class SignedTransaction {

    @LCS.Field(0)
    public abstract Transaction getTransaction();

    @LCS.Field(1)
    public abstract TransactionAuthenticator getAuthenticator();

    public SubmitTransactionRequest toGrpcObject() {
        types.TransactionOuterClass.SignedTransaction signedTransaction = types.TransactionOuterClass.SignedTransaction
                .newBuilder()
                .setTxnBytes(LCSSerializer.create().serialize(this, SignedTransaction.class).toByteString())
                .build();
        return SubmitTransactionRequest.newBuilder()
                .setTransaction(signedTransaction)
                .build();
    }

}
