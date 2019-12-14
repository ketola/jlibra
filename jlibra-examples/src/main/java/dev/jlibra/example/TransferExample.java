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

import dev.jlibra.AccountAddress;
import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignature;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.admissioncontrol.transaction.result.SubmitTransactionResult;
import dev.jlibra.move.Move;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TransferExample {

    private static final Logger logger = LogManager.getLogger(TransferExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey = KeyUtils.privateKeyFromHexString(
                "3051020101300506032b657004220420c18abb47a0577c71e50f408214df5bfec3a0969d361170023084c6c6ae295ab88121000e17c9353c32509613b43bb66aa4242ca9277445d526c98d71add83bc2751d13");
        PublicKey publicKey = KeyUtils.publicKeyFromHexString(
                "302a300506032b65700321000e17c9353c32509613b43bb66aa4242ca9277445d526c98d71add83bc2751d13");

        String toAddress = "8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d";

        long amount = 1;
        int sequenceNumber = 2;

        logger.info("Sending from {} to {}", toHexStringLibraAddress(publicKey.getEncoded()), toAddress);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(amount * 1000000);
        AccountAddressArgument addressArgument = new AccountAddressArgument(Hex.decode(toAddress));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(140000)
                .gasUnitPrice(0)
                .senderAccount(AccountAddress.ofPublicKey(publicKey))
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(ByteString.copyFrom(Move.peerToPeerTransferAsBytes()))
                        .addArguments(addressArgument, amountArgument)
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(publicKey)
                .transaction(transaction)
                .signature(ImmutableSignature.builder()
                        .privateKey(privateKey)
                        .transaction(transaction)
                        .build())
                .build();

        SubmitTransactionResult result = admissionControl.submitTransaction(signedTransaction);

        logger.info("Result: " + result);
        Thread.sleep(3000); // add sleep to prevent premature closing of channel
        channel.shutdown();
    }

}
