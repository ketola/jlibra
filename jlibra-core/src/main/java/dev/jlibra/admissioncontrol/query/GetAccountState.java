package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import types.GetWithProof.GetAccountStateRequest;
import types.GetWithProof.RequestItem;

@Value.Immutable
public abstract class GetAccountState {

    public abstract AccountAddress getAddress();

    public RequestItem toGrpcObject() {
        GetAccountStateRequest getAccountStateRequest = GetAccountStateRequest.newBuilder()
                .setAddress(getAddress().toByteString())
                .build();
        return RequestItem.newBuilder()
                .setGetAccountStateRequest(getAccountStateRequest)
                .build();
    }
}
