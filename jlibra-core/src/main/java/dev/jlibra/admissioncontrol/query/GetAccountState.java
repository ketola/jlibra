package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

import dev.jlibra.admissioncontrol.transaction.FixedLengthByteSequence;
import types.GetWithProof.GetAccountStateRequest;
import types.GetWithProof.RequestItem;

@Value.Immutable
public abstract class GetAccountState {

    public abstract FixedLengthByteSequence getAddress();

    public RequestItem toGrpcObject() {
        GetAccountStateRequest getAccountStateRequest = GetAccountStateRequest.newBuilder()
                .setAddress(getAddress().getValue().toByteString())
                .build();
        return RequestItem.newBuilder()
                .setGetAccountStateRequest(getAccountStateRequest)
                .build();
    }
}
