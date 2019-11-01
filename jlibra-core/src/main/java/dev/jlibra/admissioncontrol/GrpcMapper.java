package dev.jlibra.admissioncontrol;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import dev.jlibra.LibraHelper;
import dev.jlibra.admissioncontrol.query.AccountResource;
import dev.jlibra.admissioncontrol.query.GetAccountState;
import dev.jlibra.admissioncontrol.query.GetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.ImmutableUpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.query.SignedTransactionWithProof;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import types.GetWithProof;
import types.GetWithProof.GetAccountStateRequest;
import types.GetWithProof.GetAccountTransactionBySequenceNumberRequest;
import types.GetWithProof.RequestItem;
import types.GetWithProof.UpdateToLatestLedgerResponse;
import types.TransactionOuterClass.SignedTransaction;

public class GrpcMapper {

    public static SubmitTransactionRequest toSubmitTransactionRequest(
            dev.jlibra.admissioncontrol.transaction.SignedTransaction transaction) {
        SignedTransaction signedTransaction = SignedTransaction.newBuilder()
                .setTxnBytes(ByteString.copyFrom(transaction.serialize()))
                .build();

        return SubmitTransactionRequest.newBuilder()
                .setTransaction(signedTransaction)
                .build();
    }

    public static List<RequestItem> accountStateQueriesToRequestItems(List<GetAccountState> accountStateQueries) {
        if (accountStateQueries == null)
            return new ArrayList<>();

        return accountStateQueries.stream().map(argument -> {
            GetAccountStateRequest getAccountStateRequest = GetAccountStateRequest.newBuilder()
                    .setAddress(ByteString.copyFrom(argument.getAddress()))
                    .build();

            return RequestItem.newBuilder()
                    .setGetAccountStateRequest(getAccountStateRequest)
                    .build();
        }).collect(toList());
    }

    public static List<RequestItem> accountTransactionBySequenceNumberQueriesToRequestItems(
            List<GetAccountTransactionBySequenceNumber> accountTransactionBySequenceNumberQueries) {
        if (accountTransactionBySequenceNumberQueries == null)
            return new ArrayList<>();

        return accountTransactionBySequenceNumberQueries.stream().map(argument -> {
            GetAccountTransactionBySequenceNumberRequest getAccountTransactionBySequenceNumberRequest = GetAccountTransactionBySequenceNumberRequest
                    .newBuilder()
                    .setAccount(ByteString.copyFrom(argument.getAccountAddress()))
                    .setSequenceNumber(argument.getSequenceNumber())
                    .setFetchEvents(true)
                    .build();

            return RequestItem.newBuilder()
                    .setGetAccountTransactionBySequenceNumberRequest(getAccountTransactionBySequenceNumberRequest)
                    .build();
        }).collect(toList());
    }

    public static UpdateToLatestLedgerResult updateToLatestLedgerResponseToResult(
            UpdateToLatestLedgerResponse response) {
        List<AccountResource> accountStates = new ArrayList<>();
        List<SignedTransactionWithProof> accountTransactionsBySequenceNumber = new ArrayList<>();

        response.getResponseItemsList().forEach(responseItem -> {
            accountStates.addAll(LibraHelper.readAccountStates(responseItem.getGetAccountStateResponse()));

            addSignedTransactionWithProof(responseItem, accountTransactionsBySequenceNumber);
        });

        return ImmutableUpdateToLatestLedgerResult.builder()
                .accountResources(accountStates)
                .accountTransactionsBySequenceNumber(accountTransactionsBySequenceNumber)
                .build();
    }

    private static void addSignedTransactionWithProof(GetWithProof.ResponseItem responseItem,
            List<SignedTransactionWithProof> accumulator) {
        GetWithProof.GetAccountTransactionBySequenceNumberResponse transactionResponse = responseItem
                .getGetAccountTransactionBySequenceNumberResponse();
        if (transactionResponse.hasTransactionWithProof()) {
            accumulator.add(LibraHelper
                    .readSignedTransactionWithProof(responseItem.getGetAccountTransactionBySequenceNumberResponse()));
        }
    }

}
