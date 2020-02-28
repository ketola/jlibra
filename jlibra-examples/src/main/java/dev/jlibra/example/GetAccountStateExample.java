package dev.jlibra.example;

import static java.util.Arrays.asList;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.AccountAddress;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GetAccountStateExample {

    private static final Logger logger = LogManager.getLogger(GetAccountStateExample.class);

    public static void main(String[] args) throws Exception {
        String address = "7448b4ebbd83ea973df911e458be81f36cdea018d1b3b19d92bbf05575259a0b";

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        UpdateToLatestLedgerResult result = admissionControl
                .updateToLatestLedger(
                        ImmutableQuery.builder()
                                .accountStateQueries(asList(
                                        ImmutableGetAccountState.builder()
                                                .address(AccountAddress.fromHexString(address))
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
