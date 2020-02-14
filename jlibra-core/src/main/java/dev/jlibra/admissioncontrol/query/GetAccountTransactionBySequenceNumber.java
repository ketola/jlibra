package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import types.GetWithProof.GetAccountTransactionBySequenceNumberRequest;
import types.GetWithProof.RequestItem;

@Value.Immutable
public abstract class GetAccountTransactionBySequenceNumber {

    public abstract ByteSequence getAccountAddress();

    public abstract long getSequenceNumber();

    public abstract boolean getFetchEvents();

    public RequestItem toGrpcObject() {
        GetAccountTransactionBySequenceNumberRequest getAccountTransactionBySequenceNumberRequest = GetAccountTransactionBySequenceNumberRequest
                .newBuilder()
                .setAccount(getAccountAddress().toByteString())
                .setSequenceNumber(getSequenceNumber())
                .setFetchEvents(getFetchEvents())
                .build();

        return RequestItem.newBuilder()
                .setGetAccountTransactionBySequenceNumberRequest(getAccountTransactionBySequenceNumberRequest)
                .build();
    }
}
