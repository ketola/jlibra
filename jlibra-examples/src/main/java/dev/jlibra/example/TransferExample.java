package dev.jlibra.example;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticatioKeyPreimage;
import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransactionAuthenticator;
import dev.jlibra.admissioncontrol.transaction.Signature;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.StructTag;
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
                "3051020101300506032b6570042204207c463595de7ec2ab0c414ed81c537ee03cda0ba825f83737d2d20585a8859971812100784c1cf1a888d057f39a7db399949468a8fc5a96a1b219f4f4b7e2ac54a84d18"));
        PublicKey publicKey = KeyUtils.publicKeyFromByteSequence(ByteArray.from(
                "302a300506032b6570032100784c1cf1a888d057f39a7db399949468a8fc5a96a1b219f4f4b7e2ac54a84d18"));

        PrivateKey badPrivateKey = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b65700422042010e04d76c360608c9483349fd3802368357f9bd29d628fa3f07601d18bef411881210059f2aa4dec450d11a86c92768a159562c8107c66d70a634f58989413303ef860"));
        PublicKey badPublicKey = KeyUtils.publicKeyFromByteSequence(ByteArray.from(
                "302a300506032b657003210059f2aa4dec450d11a86c92768a159562c8107c66d70a634f58989413303ef860"));

        String toAddress = AccountAddress.fromPublicKey(badPublicKey).toString();
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
                AccountAddress.fromHexString(toAddress));
        ByteArray subseq = AuthenticatioKeyPreimage.fromPublicKey(badPublicKey).toByteArray().subseq(0, 16);
        ByteArrayArgument authkeyPrefixArgument = new ByteArrayArgument(subseq);

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(140000)
                .gasUnitPrice(0)
                .senderAccount(AccountAddress.fromPublicKey(publicKey))
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferAsBytes())
                        .addArguments(addressArgument, authkeyPrefixArgument, amountArgument)
                        .build())
                .gasSpecifier(new StructTag())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticator.builder()
                        .publicKey(dev.jlibra.PublicKey.fromPublicKey(publicKey))
                        .signature(Signature.signTransaction(transaction, privateKey))
                        .build())
                .transaction(transaction)
                .build();

        SubmitTransactionResult result = admissionControl.submitTransaction(signedTransaction);

        logger.info("Result: {}", result);
        Thread.sleep(3000); // add sleep to prevent premature closing of channel
        channel.shutdown();
    }

}
