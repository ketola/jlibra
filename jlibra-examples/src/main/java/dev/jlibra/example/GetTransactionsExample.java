package dev.jlibra.example;

import static java.util.Arrays.asList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetTransactions;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * The GetTransactions query allows you to query transaction based on the
 * version number (see:
 * https://developers.libra.org/docs/reference/glossary#version)
 * 
 */
public class GetTransactionsExample {

    private static final Logger logger = LogManager.getLogger(GetTransactionsExample.class);

    public static void main(String[] args) {
        long start = 8720763;
        long limit = 10;
        boolean fetchEvent = true;

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(ImmutableQuery.builder()
                .transactionsQueries(
                        asList(ImmutableGetTransactions.builder()
                                .startVersion(start)
                                .limit(limit)
                                .fetchEvents(fetchEvent)
                                .build()))
                .build());

        result.getTransactions().forEach(txList -> {
            logger.info("Transactions: ");
            txList.getTransactions().forEach(tx -> {
                logger.info(tx);
            });

            logger.info("Events: ");
            txList.getEvents()
                    .forEach(e -> logger.info("{}: Sequence number: {},key:{} Amount: {}, Metadata: {}",
                            e.getAccountAddress(),
                            e.getSequenceNumber(),
                            e.getKey(),
                            e.getAmount(),
                            e.getMetadata().orElse(null)));
        });

        channel.shutdown();
    }
}
