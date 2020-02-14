package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import types.GetWithProof.GetAccountStateRequest;
import types.GetWithProof.RequestItem;

@Value.Immutable
public abstract class GetAccountState {

    public abstract ByteSequence getAddress();

    public RequestItem toGrpcObject() {
        GetAccountStateRequest getAccountStateRequest = GetAccountStateRequest.newBuilder()
                .setAddress(getAddress().toByteString())
                .build();
        return RequestItem.newBuilder()
                .setGetAccountStateRequest(getAccountStateRequest)
                .build();
    }
}
