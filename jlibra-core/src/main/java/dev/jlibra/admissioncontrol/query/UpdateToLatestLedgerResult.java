package dev.jlibra.admissioncontrol.query;

import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import types.AccountStateBlobOuterClass.AccountStateWithProof;
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
            AccountStateWithProof accountStateWithProof = item.getGetAccountStateResponse().getAccountStateWithProof();
            if (accountStateWithProof.hasBlob()) {
                accountStates.addAll(AccountResource.fromGrpcObject(accountStateWithProof));
            }

            types.TransactionOuterClass.TransactionWithProof transactionWithProof = item
                    .getGetAccountTransactionBySequenceNumberResponse().getTransactionWithProof();
            if (transactionWithProof.hasProof()) {
                accountTransactionsBySequenceNumber
                        .add(TransactionWithProof.fromGrpcObject(
                                transactionWithProof));
            }
        }

        return ImmutableUpdateToLatestLedgerResult.builder()
                .accountResources(accountStates)
                .accountTransactionsBySequenceNumber(accountTransactionsBySequenceNumber)
                .build();
    }
}
