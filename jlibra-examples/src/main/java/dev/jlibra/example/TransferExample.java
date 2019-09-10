package dev.jlibra.example;

import static dev.jlibra.KeyUtils.toHexStringLibraAddress;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.encoders.Hex;

import com.google.protobuf.ByteString;

import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.transaction.AddressArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableProgram;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.SubmitTransactionResult;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.move.Move;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TransferExample {

    private static final Logger logger = LogManager.getLogger(TransferExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey = KeyUtils.privateKeyFromHexString(
                "3051020101300506032b6570042204207422e9df27029f7b83c37035622f93cd0e9b3a2a705d0745d573252756fd8c888121008e23fbceaa5b7a038c8994ca8258c8815e6e9007e3de86598cd46357e5e60024");
        PublicKey publicKey = KeyUtils.publicKeyFromHexString(
                "302a300506032b65700321008e23fbceaa5b7a038c8994ca8258c8815e6e9007e3de86598cd46357e5e60024");

        String toAddress = "8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d";

        long amount = 1;
        int sequenceNumber = 0;

        logger.info("Sending from {} to {}", toHexStringLibraAddress(publicKey.getEncoded()), toAddress);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(amount * 1000000);
        AddressArgument addressArgument = new AddressArgument(Hex.decode(toAddress));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(240000)
                .gasUnitPrice(1)
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .program(
                        ImmutableProgram.builder()
                                .code(ByteString.copyFrom(Move.peerToPeerTransferAsBytes()))
                                .addArguments(addressArgument, amountArgument)
                                .build())
                .build();

        SubmitTransactionResult result = admissionControl.submitTransaction(publicKey, privateKey,
                transaction);

        logger.info("Status type: {}", result.getStatusCase());
        logger.info("Admission control status: {}", result.getAdmissionControlStatus());
        logger.info("Mempool status: {}", result.getMempoolStatus());
        logger.info("VM status: {}", result.getVmStatus());

        channel.shutdown();
        Thread.sleep(2000); // add sleep to prevent premature closing of channel
    }

}
