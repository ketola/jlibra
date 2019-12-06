package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import dev.jlibra.AccountAddress;
import types.GetWithProof.GetAccountTransactionBySequenceNumberRequest;
import types.GetWithProof.RequestItem;

@Value.Immutable
public abstract class GetAccountTransactionBySequenceNumber {

    public abstract AccountAddress getAccountAddress();

    public abstract long getSequenceNumber();

    public RequestItem toGrpcObject() {
        GetAccountTransactionBySequenceNumberRequest getAccountTransactionBySequenceNumberRequest = GetAccountTransactionBySequenceNumberRequest
                .newBuilder()
                .setAccount(ByteString.copyFrom(getAccountAddress().asByteArray()))
                .setSequenceNumber(getSequenceNumber())
                .setFetchEvents(true)
                .build();

        return RequestItem.newBuilder()
                .setGetAccountTransactionBySequenceNumberRequest(getAccountTransactionBySequenceNumberRequest)
                .build();
    }
}
