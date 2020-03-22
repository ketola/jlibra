package dev.jlibra.example.wait.condition;

import static java.util.Arrays.asList;

import dev.jlibra.AccountAddress;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.serialization.ByteArray;

public class Account {

    public static WaitCondition exists(AdmissionControl admissionControl, AccountAddress accountAddress) {
        return () -> {
            UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(ImmutableQuery.builder()
                    .accountStateQueries(asList(ImmutableGetAccountState.builder()
                            .address(accountAddress)
                            .build()))
                    .build());
            return Boolean.valueOf(!result.getAccountStateQueryResults().isEmpty());
        };
    }

    public static WaitCondition containsAtLeast(long amountOfLibra, AccountAddress address,
            AdmissionControl admissionControl) {
        return () -> {
            UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(ImmutableQuery.builder()
                    .accountStateQueries(asList(ImmutableGetAccountState.builder()
                            .address(address)
                            .build()))
                    .build());
            return Boolean.valueOf(!result.getAccountStateQueryResults().isEmpty() && result
                    .getAccountStateQueryResults().get(0).getBalanceInMicroLibras() > amountOfLibra * 1_000_000);
        };
    }

    public static WaitCondition authenticationKeyEquals(ByteArray authenticationKey, AccountAddress address,
            AdmissionControl admissionControl) {
        return () -> {
            UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(ImmutableQuery.builder()
                    .accountStateQueries(asList(ImmutableGetAccountState.builder()
                            .address(address)
                            .build()))
                    .build());
            return Boolean.valueOf(!result.getAccountStateQueryResults().isEmpty() && result
                    .getAccountStateQueryResults().get(0).getAuthenticationKey().equals(authenticationKey));
        };
    }

}
