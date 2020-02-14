package dev.jlibra.example;

import static java.util.Arrays.asList;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.serialization.ByteSequence;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GetAccountStateExample {

    private static final Logger logger = LogManager.getLogger(GetAccountStateExample.class);

    public static void main(String[] args) throws Exception {
        String address = "1b2d1a2b57704043fa1f97fcc08e268f45d1c5b9f7b43c481941c103b99d8ca5";

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        UpdateToLatestLedgerResult result = admissionControl
                .updateToLatestLedger(
                        ImmutableQuery.builder()
                                .accountStateQueries(asList(
                                        ImmutableGetAccountState.builder()
                                                .address(ByteSequence.from(address))
                                                .build()))
                                .build());

        result.getAccountStateQueryResults().forEach(accountResource -> {
            logger.info("Authentication key: {}", accountResource.getAuthenticationKey());
            logger.info("Received events: {}", accountResource.getReceivedEvents().getCount());
            logger.info("Sent events: {}", accountResource.getSentEvents().getCount());
            logger.info("Balance (microLibras): {}", accountResource.getBalanceInMicroLibras());
            logger.info("Balance (Libras): {}", new BigDecimal(accountResource.getBalanceInMicroLibras())
                    .divide(BigDecimal.valueOf(1_000_000)));
            logger.info("Sequence number: {}", accountResource.getSequenceNumber());
            logger.info("Delegated withdrawal capability: {}", accountResource.getDelegatedWithdrawalCapability());
            logger.info("Delegated key rotation capability: {}", accountResource.getDelegatedKeyRotationCapability());
        });

        channel.shutdown();
    }
}
