package dev.jlibra.admissioncontrol.query;

import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import types.GetWithProof.ResponseItem;
import types.GetWithProof.UpdateToLatestLedgerResponse;

@Value.Immutable
public abstract class UpdateToLatestLedgerResult {

    public abstract List<AccountResource> getAccountResources();

    public abstract List<TransactionWithProof> getAccountTransactionsBySequenceNumber();

    public static UpdateToLatestLedgerResult fromGrpcObject(UpdateToLatestLedgerResponse grpcObject) {
        List<AccountResource> accountStates = new ArrayList<>();
        List<TransactionWithProof> accountTransactionsBySequenceNumber = new ArrayList<>();

        for (ResponseItem item : grpcObject.getResponseItemsList()) {
            accountStates.addAll(AccountResource.fromGrpcObject(
                    item.getGetAccountStateResponse().getAccountStateWithProof()));
            accountTransactionsBySequenceNumber
                    .add(TransactionWithProof.fromGrpcObject(
                            item.getGetAccountTransactionBySequenceNumberResponse().getTransactionWithProof()));
        }

        return ImmutableUpdateToLatestLedgerResult.builder()
                .accountResources(accountStates)
                .accountTransactionsBySequenceNumber(accountTransactionsBySequenceNumber)
                .build();
    }
}
