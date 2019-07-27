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

public class GetAccountStateExample {

    private static final Logger logger = LogManager.getLogger(GetAccountStateExample.class);

    public static void main(String[] args) {
        String address = "045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98";

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

        result.getAccountStates().forEach(accountState -> {
            logger.info("Address: {}", Hex.toHexString(accountState.getAddress()));
            logger.info("Received events: {}", accountState.getReceivedEvents());
            logger.info("Sent events: {}", accountState.getSentEvents());
            logger.info("Balance (microLibras): {}", accountState.getBalanceInMicroLibras());
            logger.info("Balance (Libras): {}", new BigDecimal(accountState.getBalanceInMicroLibras()).divide(BigDecimal.valueOf(1000000)));
            logger.info("Sequence number: {}", accountState.getSequenceNumber());
        });

        channel.shutdown();
    }

}
