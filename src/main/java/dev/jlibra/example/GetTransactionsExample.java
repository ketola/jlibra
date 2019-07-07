package dev.jlibra.example;

import java.io.IOException;
import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlGrpc;
import admission_control.AdmissionControlGrpc.AdmissionControlBlockingStub;
import dev.jlibra.LibraHelper;
import dev.jlibra.PaymentEvent;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import types.GetWithProof.GetAccountTransactionBySequenceNumberRequest;
import types.GetWithProof.RequestItem;
import types.GetWithProof.UpdateToLatestLedgerRequest;
import types.GetWithProof.UpdateToLatestLedgerResponse;

public class GetTransactionsExample {

    public static void main(String[] args) throws IOException {
        String address = "6674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c";

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);

        RequestItem requestItem = RequestItem.newBuilder()
                .setGetAccountTransactionBySequenceNumberRequest(
                        GetAccountTransactionBySequenceNumberRequest.newBuilder()
                                .setAccount(ByteString.copyFrom(Hex.decode(address)))
                                .setFetchEvents(true)
                                .setSequenceNumber(37)
                                .build())
                .build();

        UpdateToLatestLedgerResponse response = stub.updateToLatestLedger(UpdateToLatestLedgerRequest.newBuilder()
                .addAllRequestedItems(Arrays.asList(requestItem))
                .build());

        response.getResponseItemsList().forEach(responseItem -> {
            responseItem.getGetAccountTransactionBySequenceNumberResponse().getSignedTransactionWithProof().getEvents()
                    .getEventsList()
                    .forEach(info -> {

                        PaymentEvent paymentEvent = LibraHelper.readPaymentEvent(info);

                        System.out.println("Address: " + new String(Hex.encode(paymentEvent.getAddress())));
                        System.out.println("Amount: " + paymentEvent.getAmount());
                        System.out.println(paymentEvent.getEventPath().getSuffix());
                    });
        });

    }

}
