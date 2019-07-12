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
import dev.jlibra.KeyUtils;
import dev.jlibra.LibraHelper;
import dev.jlibra.admissioncontrol.TransactionArgument.Type;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import types.Transaction.Program;
import types.Transaction.RawTransaction;
import types.Transaction.SignedTransaction;
import types.Transaction.TransactionArgument;
import types.Transaction.TransactionArgument.ArgType;

public class AdmissionControl {

    private static Map<Type, ArgType> jlibraArgumentTypeToGrpcArgumentType;

    static {
        jlibraArgumentTypeToGrpcArgumentType = new HashMap<>();
        jlibraArgumentTypeToGrpcArgumentType.put(Type.ADDRESS, ArgType.ADDRESS);
        jlibraArgumentTypeToGrpcArgumentType.put(Type.U64, ArgType.U64);
    }

    public Result sendTransaction(PublicKey publicKey, PrivateKey privateKey, Transaction transaction) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
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
                .setExpirationTime(600)
                .setGasUnitPrice(1)
                .setMaxGasAmount(6000)
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

        return new Result(response.getAcStatus(), response.getMempoolStatus(), response.getVmStatus());

    }

    private ByteString readCodeFromStream(Transaction transaction) {
        try {
            return ByteString.readFrom(transaction.getProgram().getCode());
        } catch (IOException e) {
            throw new RuntimeException("Could not read move code from input stream", e);
        }
    }

}
