package dev.jlibra.example;

import static java.util.Arrays.asList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.AccountAddress;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.serialization.ByteSequence;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GetAccountTransactionBySequenceNumberExample {

    private static final Logger logger = LogManager.getLogger(GetAccountTransactionBySequenceNumberExample.class);

    public static void main(String[] args) {
        // This can be a "transaction with Metadata" or a standard transfer, for the
        // standard one metadata is empty.
        String address = "0703a61585597d9b56a46a658464738dff58222b4393d32dd9899bedb58666e9";
        int sequenceNumber = 70;

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(ImmutableQuery.builder()
                .accountTransactionBySequenceNumberQueries(
                        asList(ImmutableGetAccountTransactionBySequenceNumber.builder()
                                .accountAddress(AccountAddress.ofByteSequence(ByteSequence.from(address)))
                                .sequenceNumber(sequenceNumber)
                                .build()))
                .build());

        result.getAccountTransactionsBySequenceNumber().forEach(tx -> tx.getEvents()
                .forEach(e -> logger.info("{}: Sequence number: {}, Amount: {}, Metadata: {}",
                        e.getAccountAddress(), e.getSequenceNumber(), e.getAmount(), e.getMetadata().orElse(null))));

        channel.shutdown();
    }
}
