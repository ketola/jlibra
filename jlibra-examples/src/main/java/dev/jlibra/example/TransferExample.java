package dev.jlibra.example;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.AccountAddress;
import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.Signature;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.admissioncontrol.transaction.result.SubmitTransactionResult;
import dev.jlibra.move.Move;
import dev.jlibra.serialization.ByteArray;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class TransferExample {

    private static final Logger logger = LogManager.getLogger(TransferExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b6570042204203093fee07b354989bcab0a57cf30b103ec071ddf507113bd1ea31c69e0d146338121006b0416ca96bd0fa06cd48e72f5279db81ec1dc46fed1befdebb5194b25fc38d8"));
        PublicKey publicKey = KeyUtils.publicKeyFromByteSequence(ByteArray.from(
                "302a300506032b65700321006b0416ca96bd0fa06cd48e72f5279db81ec1dc46fed1befdebb5194b25fc38d8"));

        String toAddress = "7448b4ebbd83ea973df911e458be81f36cdea018d1b3b19d92bbf05575259a0b";

        long amount = 1;
        int sequenceNumber = 0;

        logger.info("Sending from {} to {}", AccountAddress.fromPublicKey(publicKey), toAddress);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(amount * 1000000);
        AccountAddressArgument addressArgument = new AccountAddressArgument(
                AccountAddress.ofHexString(toAddress));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(140000)
                .gasUnitPrice(0)
                .senderAccount(AccountAddress.fromPublicKey(publicKey))
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferAsBytes())
                        .addArguments(addressArgument, amountArgument)
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(dev.jlibra.PublicKey.ofPublicKey(publicKey))
                .transaction(transaction)
                .signature(Signature.signTransaction(transaction, privateKey))
                .build();

        SubmitTransactionResult result = admissionControl.submitTransaction(signedTransaction);

        logger.info("Result: {}", result);
        Thread.sleep(3000); // add sleep to prevent premature closing of channel
        channel.shutdown();
    }

}
