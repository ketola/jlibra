package dev.jlibra.example;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlGrpc;
import admission_control.AdmissionControlGrpc.AdmissionControlBlockingStub;
import dev.jlibra.AccountState;
import dev.jlibra.LibraHelper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import types.GetWithProof.GetAccountStateRequest;
import types.GetWithProof.RequestItem;
import types.GetWithProof.UpdateToLatestLedgerRequest;
import types.GetWithProof.UpdateToLatestLedgerResponse;

public class GetAccountStateExample {

    public static void main(String[] args) throws IOException {
        String address = "045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98";

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);

        GetAccountStateRequest getAccountStateRequest = GetAccountStateRequest.newBuilder()
                .setAddress(ByteString.copyFrom(Hex.decode(address)))
                .build();

        RequestItem requestItem = RequestItem.newBuilder()
                .setGetAccountStateRequest(getAccountStateRequest)
                .build();

        UpdateToLatestLedgerResponse response = stub.updateToLatestLedger(UpdateToLatestLedgerRequest.newBuilder()
                .addAllRequestedItems(Arrays.asList(requestItem))
                .build());

        List<AccountState> accountStates = new ArrayList<>();

        response.getResponseItemsList().forEach(responseItem -> {
            accountStates.addAll(LibraHelper.readAccountStates(responseItem.getGetAccountStateResponse()
                    .getAccountStateWithProof()));
        });

        accountStates.forEach(accountState -> {
            System.out.println("Address:" + new String(Hex.encode(accountState.getAddress())));
            System.out.println("Received events: " + accountState.getReceivedEvents());
            System.out.println("Sent events: " + accountState.getSentEvents());
            System.out.println("Balance (microLibras): " + accountState.getBalanceInMicroLibras());
            System.out.println("Balance (Libras): "
                    + new BigDecimal(accountState.getBalanceInMicroLibras()).divide(BigDecimal.valueOf(1000000)));
            System.out.println("Sequence number: " + accountState.getSequenceNumber());
            System.out.println();
        });

    }

}
