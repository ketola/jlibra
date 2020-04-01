package dev.jlibra.example;

import static java.util.Arrays.asList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.AccountAddress;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GetAccountTransactionBySequenceNumberExample {

    private static final Logger logger = LogManager.getLogger(GetAccountTransactionBySequenceNumberExample.class);

    public static void main(String[] args) {
        String address = "4e03aec69589026b4a095c9cd2e53ca6";
        int sequenceNumber = 0;

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(ImmutableQuery.builder()
                .accountTransactionBySequenceNumberQueries(
                        asList(ImmutableGetAccountTransactionBySequenceNumber.builder()
                                .accountAddress(AccountAddress.fromHexString(address))
                                .sequenceNumber(sequenceNumber)
                                .fetchEvents(true)
                                .build()))
                .build());

        result.getAccountTransactionBySequenceNumberQueryResults().forEach(tx -> {
            logger.info("Version: " + tx.getVersion());
            logger.info(tx.getTransaction());
            logger.info("Events: ");
            tx.getEvents()
                    .forEach(e -> logger.info("{}: Sequence number: {}, Amount: {}, Metadata: {}",
                            e.getAccountAddress(), e.getSequenceNumber(), e.getAmount(), e.getMetadata().orElse(null)));
        });

        channel.shutdown();
    }
}
