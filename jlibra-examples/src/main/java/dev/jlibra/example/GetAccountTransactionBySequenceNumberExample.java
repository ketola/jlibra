package dev.jlibra.example;

import java.io.IOException;

import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GetAccountTransactionBySequenceNumberExample {

    public static void main(String[] args) throws IOException {
        String address = "6674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c";
        int sequenceNumber = 3;

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(ImmutableQuery.builder()
                .addAccountTransactionBySequenceNumberQueries(
                        ImmutableGetAccountTransactionBySequenceNumber.builder()
                                .accountAddress(Hex.decode(address))
                                .sequenceNumber(sequenceNumber)
                                .build())
                .build());

        result.getAccountTransactionsBySequenceNumber().forEach(tx -> {
            System.out.println("Sender public key: " + Hex.toHexString(tx.getSenderPublicKey()));
            System.out.println("Sender signature: " + Hex.toHexString(tx.getSenderSignature()));

            tx.getEvents().forEach(e -> {
                System.out
                        .println(Hex.toHexString(e.getAddress()) + " " + e.getEventPath().getEventType()
                                + " Amount: " + e.getAmount());
            });

        });

        channel.shutdown();

    }

}
