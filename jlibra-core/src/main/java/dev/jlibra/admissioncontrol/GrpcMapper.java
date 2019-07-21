package dev.jlibra.admissioncontrol;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import com.google.protobuf.ByteString;
import dev.jlibra.AccountState;
import dev.jlibra.KeyUtils;
import dev.jlibra.LibraHelper;
import dev.jlibra.admissioncontrol.query.*;
import dev.jlibra.admissioncontrol.transaction.RawTransactionSigner;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.mnemonic.ExtendedPrivKey;
import org.bouncycastle.util.encoders.Hex;
import types.GetWithProof.GetAccountStateRequest;
import types.GetWithProof.GetAccountTransactionBySequenceNumberRequest;
import types.GetWithProof.RequestItem;
import types.GetWithProof.UpdateToLatestLedgerResponse;
import types.Transaction.Program;
import types.Transaction.RawTransaction;
import types.Transaction.SignedTransaction;
import types.Transaction.TransactionArgument;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class GrpcMapper {

    public static SubmitTransactionRequest toSubmitTransactionRequest(ExtendedPrivKey fromAccount, Transaction transaction, RawTransactionSigner signer) {
        ByteString senderAccount = ByteString.copyFrom(Hex.decode(fromAccount.getAddress()));
        ByteString senderPublicKey = ByteString.copyFrom(fromAccount.publicKey.getData());

        return toSubmitTransactionRequest(senderAccount, senderPublicKey, transaction, signer);
    }

    public static SubmitTransactionRequest toSubmitTransactionRequest(PublicKey publicKey, PrivateKey privateKey, Transaction transaction) {
        ByteString senderAccount = ByteString.copyFrom(KeyUtils.toByteArrayLibraAddress(publicKey.getEncoded()));
        ByteString senderPublicKey = ByteString.copyFrom(KeyUtils.stripPublicKeyPrefix(publicKey.getEncoded()));
        RawTransactionSigner signer = rtx -> LibraHelper.signTransaction(rtx, privateKey);

        return toSubmitTransactionRequest(senderAccount, senderPublicKey, transaction, signer);
    }

    public static SubmitTransactionRequest toSubmitTransactionRequest(ByteString senderAccount, ByteString senderPublicKey, Transaction transaction, RawTransactionSigner signer) {
        List<TransactionArgument> transactionArguments = transaction.getProgram().getArguments().stream()
                .map(txa -> txa.toGrpcTransactionArgument())
                .collect(toList());

        Program program = Program.newBuilder()
                .addAllArguments(transactionArguments)
                .setCode(transaction.getProgram().getCode())
                .addAllModules(new ArrayList<>())
                .build();

        RawTransaction rawTransaction = RawTransaction.newBuilder()
                .setProgram(program)
                .setExpirationTime(transaction.getExpirationTime())
                .setGasUnitPrice(transaction.getGasUnitPrice())
                .setMaxGasAmount(transaction.getMaxGasAmount())
                .setSenderAccount(senderAccount)
                .setSequenceNumber(transaction.getSequenceNumber())
                .build();

        ByteString signature = ByteString.copyFrom(signer.apply(rawTransaction));

        SignedTransaction signedTransaction = SignedTransaction.newBuilder()
                .setRawTxnBytes(rawTransaction.toByteString())
                .setSenderPublicKey(senderPublicKey)
                .setSenderSignature(signature)
                .build();

        SubmitTransactionRequest submitTransactionRequest = SubmitTransactionRequest.newBuilder()
                .setSignedTxn(signedTransaction)
                .build();

        return submitTransactionRequest;
    }


    public static List<RequestItem> accountStateQueriesToRequestItems(List<GetAccountState> accountStateQueries) {
        if (accountStateQueries == null)
            return new ArrayList<>();

        return accountStateQueries.stream().map(argument -> {
            GetAccountStateRequest getAccountStateRequest = GetAccountStateRequest.newBuilder()
                    .setAddress(ByteString.copyFrom(argument.getAddress()))
                    .build();

            RequestItem requestItem = RequestItem.newBuilder()
                    .setGetAccountStateRequest(getAccountStateRequest)
                    .build();
            return requestItem;
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

            RequestItem requestItem = RequestItem.newBuilder()
                    .setGetAccountTransactionBySequenceNumberRequest(getAccountTransactionBySequenceNumberRequest)
                    .build();
            return requestItem;
        }).collect(toList());
    }

    public static UpdateToLatestLedgerResult updateToLatestLedgerResponseToResult(
            UpdateToLatestLedgerResponse response) {
        List<AccountState> accountStates = new ArrayList<>();
        List<SignedTransactionWithProof> accountTransactionsBySequenceNumber = new ArrayList<>();

        response.getResponseItemsList().forEach(responseItem -> {
            accountStates.addAll(LibraHelper.readAccountStates(responseItem.getGetAccountStateResponse()));

            accountTransactionsBySequenceNumber.add(LibraHelper
                    .readSignedTransactionWithProof(responseItem.getGetAccountTransactionBySequenceNumberResponse()));
        });

        return ImmutableUpdateToLatestLedgerResult.builder()
                .accountStates(accountStates)
                .accountTransactionsBySequenceNumber(accountTransactionsBySequenceNumber)
                .build();
    }

}
