package dev.jlibra.example;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlGrpc;
import admission_control.AdmissionControlGrpc.AdmissionControlBlockingStub;
import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse;
import dev.jlibra.LibraHelper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import types.Transaction.Program;
import types.Transaction.RawTransaction;
import types.Transaction.SignedTransaction;
import types.Transaction.TransactionArgument;
import types.Transaction.TransactionArgument.ArgType;

public class TransferExample {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey = LibraHelper.privateKeyFromHexString(
                "3051020101300506032b65700422042028b947a9c0780f2cbac06ee0e4625f185871b44d9906faf41183eb08a8e2729e812100a395692a41e033f0cf28fa9d07de53075354ab15a81e34e0782b042ad9ac50db");
        PublicKey publicKey = LibraHelper.publicKeyFromHexString(
                "302a300506032b6570032100a395692a41e033f0cf28fa9d07de53075354ab15a81e34e0782b042ad9ac50db");
        String fromAddress = "64e56e6820de313eb6149d46e95bf26c697e48e723281584573e6f1c8ab31814";

        String toAddress = "045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98";
        long amount = 100;

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);

        TransactionArgument arg = TransactionArgument.newBuilder()
                .setType(ArgType.ADDRESS)
                .setData(ByteString.copyFrom(Hex.decode(toAddress)))
                .build();

        TransactionArgument arg2 = TransactionArgument.newBuilder()
                .setType(ArgType.U64)
                .setData(ByteString
                        .copyFrom(ByteBuffer.wrap(new byte[8]).order(ByteOrder.BIG_ENDIAN).putLong(amount).array()))
                .build();

        Program program = Program.newBuilder().addAllArguments(Arrays.asList(arg, arg2))
                .setCode(ByteString.copyFrom(LibraHelper.transferMoveScript()))
                .addAllModules(new ArrayList<ByteString>())
                .build();

        RawTransaction rawTransaction = RawTransaction.newBuilder()
                .setProgram(program)
                .setExpirationTime(0)
                .setGasUnitPrice(1)
                .setMaxGasAmount(6000)
                .setSenderAccount(ByteString.copyFrom(Hex.decode(fromAddress)))
                .setSequenceNumber(1)
                .build();

        SignedTransaction signedTransaction = SignedTransaction.newBuilder()
                .setRawTxnBytes(rawTransaction.toByteString())
                .setSenderPublicKey(ByteString.copyFrom(LibraHelper.stripPrefix(publicKey)))
                .setSenderSignature(ByteString.copyFrom(LibraHelper.signTransaction(rawTransaction, privateKey)))
                .build();

        SubmitTransactionRequest submitTransactionRequest = SubmitTransactionRequest.newBuilder()
                .setSignedTxn(signedTransaction)
                .build();

        SubmitTransactionResponse response = stub.submitTransaction(submitTransactionRequest);

        System.out.println("response: " + response);

        channel.shutdown();
    }
}
