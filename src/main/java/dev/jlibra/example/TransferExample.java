package dev.jlibra.example;

import java.nio.ByteBuffer;
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
                "3051020101300506032b6570042204207422e9df27029f7b83c37035622f93cd0e9b3a2a705d0745d573252756fd8c888121008e23fbceaa5b7a038c8994ca8258c8815e6e9007e3de86598cd46357e5e60024");
        PublicKey publicKey = LibraHelper.publicKeyFromHexString(
                "302a300506032b65700321008e23fbceaa5b7a038c8994ca8258c8815e6e9007e3de86598cd46357e5e60024");
        String fromAddress = "6674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c";

        String toAddress = "045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98";
        long amount = 200;

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
                        .copyFrom(ByteBuffer.allocate(Long.BYTES).putLong(amount).array()))
                .build();

        Program program = Program.newBuilder()
                .addAllArguments(Arrays.asList(arg, arg2))
                .setCode(ByteString.copyFrom(LibraHelper.transferMoveScript()))
                .addAllModules(new ArrayList<ByteString>())
                .build();

        RawTransaction rawTransaction = RawTransaction.newBuilder()
                .setProgram(program)
                .setExpirationTime(600)
                .setGasUnitPrice(1)
                .setMaxGasAmount(6000)
                .setSenderAccount(ByteString.copyFrom(Hex.decode(fromAddress)))
                .setSequenceNumber(2)
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
