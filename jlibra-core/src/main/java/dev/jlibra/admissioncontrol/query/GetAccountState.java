package dev.jlibra.admissioncontrol.query;

import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import types.GetWithProof.GetAccountStateRequest;
import types.GetWithProof.RequestItem;

@Value.Immutable
public interface GetAccountState {

    byte[] getAddress();

    default RequestItem toGrpcObject() {
        GetAccountStateRequest getAccountStateRequest = GetAccountStateRequest.newBuilder()
                .setAddress(ByteString.copyFrom(getAddress()))
                .build();
        return RequestItem.newBuilder()
                .setGetAccountStateRequest(getAccountStateRequest)
                .build();
    }
}
