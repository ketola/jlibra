package dev.jlibra.example;

import java.io.IOException;
import java.math.BigDecimal;

import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;

public class GetAccountStateExample {

    public static void main(String[] args) throws IOException {
        String address = "045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98";

        AdmissionControl admissionControl = new AdmissionControl("ac.testnet.libra.org", 8000);

        UpdateToLatestLedgerResult result = admissionControl
                .updateToLatestLedger(ImmutableQuery.builder()
                        .addAccountStateQueries(ImmutableGetAccountState.builder()
                                .address(Hex.decode(address))
                                .build())
                        .build());

        result.getAccountStates().forEach(accountState -> {
            System.out.println("Address:" + Hex.toHexString(accountState.getAddress()));
            System.out.println("Received events: " + accountState.getReceivedEvents());
            System.out.println("Sent events: " + accountState.getSentEvents());
            System.out.println("Balance (microLibras): " + accountState.getBalanceInMicroLibras());
            System.out.println("Balance (Libras): "
                    + new BigDecimal(accountState.getBalanceInMicroLibras()).divide(BigDecimal.valueOf(1000000)));
            System.out.println("Sequence number: " + accountState.getSequenceNumber());
            System.out.println();
        });

    }

}
