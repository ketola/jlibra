package dev.jlibra.admissioncontrol;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlGrpc;
import admission_control.AdmissionControlGrpc.AdmissionControlBlockingStub;
import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse;
import dev.jlibra.AccountState;
import dev.jlibra.KeyUtils;
import dev.jlibra.LibraHelper;
import dev.jlibra.admissioncontrol.query.GetAccountState;
import dev.jlibra.admissioncontrol.query.GetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.Query;
import dev.jlibra.admissioncontrol.query.SignedTransactionWithProof;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.SubmitTransactionResult;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.TransactionArgument.Type;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import types.GetWithProof.GetAccountStateRequest;
import types.GetWithProof.GetAccountTransactionBySequenceNumberRequest;
import types.GetWithProof.RequestItem;
import types.GetWithProof.UpdateToLatestLedgerRequest;
import types.GetWithProof.UpdateToLatestLedgerResponse;
import types.Transaction.Program;
import types.Transaction.RawTransaction;
import types.Transaction.SignedTransaction;
import types.Transaction.TransactionArgument;
import types.Transaction.TransactionArgument.ArgType;

public class AdmissionControl {

    private String host;

    private int port;

    public AdmissionControl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private static Map<Type, ArgType> jlibraArgumentTypeToGrpcArgumentType;

    static {
        jlibraArgumentTypeToGrpcArgumentType = new HashMap<>();
        jlibraArgumentTypeToGrpcArgumentType.put(Type.ADDRESS, ArgType.ADDRESS);
        jlibraArgumentTypeToGrpcArgumentType.put(Type.U64, ArgType.U64);
    }

    public SubmitTransactionResult submitTransaction(PublicKey publicKey, PrivateKey privateKey,
            Transaction transaction) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);

        List<TransactionArgument> transactionArguments = transaction.getProgram().getArguments().stream()
                .map(txArgument -> TransactionArgument.newBuilder()
                        .setType(jlibraArgumentTypeToGrpcArgumentType.get(txArgument.type()))
                        .setData(ByteString.copyFrom(txArgument.toByteArray()))
                        .build())
                .collect(toList());

        Program program = Program.newBuilder()
                .addAllArguments(transactionArguments)
                .setCode(readCodeFromStream(transaction))
                .addAllModules(new ArrayList<ByteString>())
                .build();

        RawTransaction rawTransaction = RawTransaction.newBuilder()
                .setProgram(program)
                .setExpirationTime(transaction.getExpirationTime())
                .setGasUnitPrice(transaction.getGasUnitPrice())
                .setMaxGasAmount(transaction.getMaxGasAmount())
                .setSenderAccount(ByteString.copyFrom(KeyUtils.toByteArrayLibraAddress(publicKey.getEncoded())))
                .setSequenceNumber(transaction.getSequenceNumber())
                .build();

        SignedTransaction signedTransaction = SignedTransaction.newBuilder()
                .setRawTxnBytes(rawTransaction.toByteString())
                .setSenderPublicKey(ByteString.copyFrom(KeyUtils.stripPublicKeyPrefix(publicKey.getEncoded())))
                .setSenderSignature(ByteString.copyFrom(LibraHelper.signTransaction(rawTransaction, privateKey)))
                .build();

        SubmitTransactionRequest submitTransactionRequest = SubmitTransactionRequest.newBuilder()
                .setSignedTxn(signedTransaction)
                .build();

        SubmitTransactionResponse response = stub.submitTransaction(submitTransactionRequest);

        channel.shutdown();

        return new SubmitTransactionResult(response.getAcStatus(), response.getMempoolStatus(), response.getVmStatus());
    }

    private ByteString readCodeFromStream(Transaction transaction) {
        try {
            return ByteString.readFrom(transaction.getProgram().getCode());
        } catch (IOException e) {
            throw new RuntimeException("Could not read move code from input stream", e);
        }
    }

    public UpdateToLatestLedgerResult updateToLatestLedger(Query query) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);

        List<RequestItem> requestItems = accountStateQueriesToRequestItems(query.getAccountStateQueries());

        requestItems.addAll(
                accountTransactionBySequenceNumberQueriesToRequestItems(
                        query.getAccountTransactionBySequenceNumberQueries()));

        UpdateToLatestLedgerResponse response = stub.updateToLatestLedger(UpdateToLatestLedgerRequest.newBuilder()
                .addAllRequestedItems(requestItems)
                .build());

        return updateToLatestLedgerResponseToResult(response);
    }

    private UpdateToLatestLedgerResult updateToLatestLedgerResponseToResult(UpdateToLatestLedgerResponse response) {
        List<AccountState> accountStates = new ArrayList<>();
        List<SignedTransactionWithProof> accountTransactionsBySequenceNumber = new ArrayList<>();

        response.getResponseItemsList().forEach(responseItem -> {
            accountStates.addAll(LibraHelper.readAccountStates(responseItem.getGetAccountStateResponse()));

            accountTransactionsBySequenceNumber.add(LibraHelper
                    .readSignedTransactionWithProof(responseItem.getGetAccountTransactionBySequenceNumberResponse()));
        });

        UpdateToLatestLedgerResult result = UpdateToLatestLedgerResult.create()
                .withAccountStates(accountStates)
                .withAccountTransactionsBySequenceNumber(accountTransactionsBySequenceNumber);
        return result;
    }

    private List<RequestItem> accountStateQueriesToRequestItems(List<GetAccountState> accountStateQueries) {
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

    private List<RequestItem> accountTransactionBySequenceNumberQueriesToRequestItems(
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
}
