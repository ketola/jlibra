package dev.jlibra.example;

import static java.util.Arrays.asList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.AccountAddress;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.GetEventsByEventAccessPath.Path;
import dev.jlibra.admissioncontrol.query.ImmutableGetEventsByEventAccessPath;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GetEventsByEventAccessPathExample {
    private static final Logger logger = LogManager.getLogger(GetEventsByEventAccessPathExample.class);

    public static void main(String[] args) {
        String address = "0703a61585597d9b56a46a658464738dff58222b4393d32dd9899bedb58666e9";

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        UpdateToLatestLedgerResult result = admissionControl
                .updateToLatestLedger(
                        ImmutableQuery.builder()
                                .eventsByEventAccessPathQueries(
                                        asList(ImmutableGetEventsByEventAccessPath.builder()
                                                .accountAddress(AccountAddress.ofHexString(address))
                                                .limit(100)
                                                .isAscending(true)
                                                .startEventSequenceNumber(0)
                                                .path(Path.RECEIVED_EVENTS)
                                                .build()))
                                .build());

        result.getEventsByEventAccessPathQueryResults().forEach(e -> {
            logger.info("Event: {}", e);
        });

        channel.shutdown();

    }

}
