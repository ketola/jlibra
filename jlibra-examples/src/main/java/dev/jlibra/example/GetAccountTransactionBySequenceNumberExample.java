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
        String address = "d5586b1c04555911fb3c0ab6f60261ad242b3eb4d0eddd2ba22c02174d6173c4";
        int sequenceNumber = 3;

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

        result.getAccountTransactionsBySequenceNumber().forEach(tx -> {
            logger.info("Version: " + tx.getVersion());
            logger.info(tx.getTransaction());
            logger.info("Events: ");
            tx.getEvents()
                    .forEach(e -> logger.info("{}: Sequence number: {}, Amount: {}",
                            e.getAccountAddress(), e.getSequenceNumber(), e.getAmount()));
        });

        channel.shutdown();
    }
}
