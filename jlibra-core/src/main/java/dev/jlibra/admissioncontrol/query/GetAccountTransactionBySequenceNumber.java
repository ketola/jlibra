package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

import dev.jlibra.admissioncontrol.transaction.FixedLengthByteSequence;
import types.GetWithProof.GetAccountTransactionBySequenceNumberRequest;
import types.GetWithProof.RequestItem;

@Value.Immutable
public abstract class GetAccountTransactionBySequenceNumber {

    public abstract FixedLengthByteSequence getAccountAddress();

    public abstract long getSequenceNumber();

    public abstract boolean getFetchEvents();

    public RequestItem toGrpcObject() {
        GetAccountTransactionBySequenceNumberRequest getAccountTransactionBySequenceNumberRequest = GetAccountTransactionBySequenceNumberRequest
                .newBuilder()
                .setAccount(getAccountAddress().getValue().toByteString())
                .setSequenceNumber(getSequenceNumber())
                .setFetchEvents(getFetchEvents())
                .build();

        return RequestItem.newBuilder()
                .setGetAccountTransactionBySequenceNumberRequest(getAccountTransactionBySequenceNumberRequest)
                .build();
    }
}
