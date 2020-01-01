package dev.jlibra.example;

import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetTransactions;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.Arrays.asList;

public class GetTransactionsExample {

    private static final Logger logger = LogManager.getLogger(GetTransactionsExample.class);

    public static void main(String[] args) {
        long start=8720763;
        long limit=10;
        boolean fetchEvent=true;

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(ImmutableQuery.builder()
                .transactions(
                        asList(ImmutableGetTransactions.builder()
                                .startVersion(start)
                                .limit(limit)
                                .fetchEvents(fetchEvent)
                                .build()))
                .build()
        );

        result.getTransactions().forEach(tx -> { tx.getEvents()
                .forEach(e -> logger.info("{}: Sequence number: {},key:{} Amount: {}",
                    e.getAccountAddress(), e.getSequenceNumber(),e.getKey(), e.getAmount()));
        });

        channel.shutdown();
    }
}
