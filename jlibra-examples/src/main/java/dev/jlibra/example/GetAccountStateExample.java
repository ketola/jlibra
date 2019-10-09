package dev.jlibra.example;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static java.math.RoundingMode.DOWN;

public class GetAccountStateExample {

    private static final Logger logger = LogManager.getLogger(GetAccountStateExample.class);

    public static void main(String[] args) {
        String address = "8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d";

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        UpdateToLatestLedgerResult result = admissionControl
                .updateToLatestLedger(ImmutableQuery.builder()
                        .addAccountStateQueries(ImmutableGetAccountState.builder()
                                .address(Hex.decode(address))
                                .build())
                        .build());

        result.getAccountResources().forEach(accountResource -> {
            logger.info("Authentication key: {}", Hex.toHexString(accountResource.getAuthenticationKey()));
            logger.info("Received events: {}", accountResource.getReceivedEvents().getCount());
            logger.info("Sent events: {}", accountResource.getSentEvents().getCount());
            logger.info("Balance (microLibras): {}", accountResource.getBalanceInMicroLibras());
            logger.info("Balance (Libras): {}", new BigDecimal(accountResource.getBalanceInMicroLibras()).divide(BigDecimal.valueOf(1000000), DOWN));
            logger.info("Sequence number: {}", accountResource.getSequenceNumber());
            logger.info("Delegated withdrawal capability: {}", accountResource.getDelegatedWithdrawalCapability());
            logger.info("Delegated key rotation capability: {}", accountResource.getDelegatedKeyRotationCapability());
        });

        channel.shutdown();
    }
}
