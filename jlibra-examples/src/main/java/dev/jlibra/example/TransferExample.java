package dev.jlibra.example;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransactionAuthenticatorEd25519;
import dev.jlibra.admissioncontrol.transaction.Signature;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.client.LibraClient;
import dev.jlibra.move.Move;
import dev.jlibra.serialization.ByteArray;

public class TransferExample {

    private static final Logger logger = LogManager.getLogger(TransferExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b657004220420d1036d8905daf0ef40f349f76885c290475e818798c2b58a74c34d94e6e8c29481210081baa2b679aa2c8fb75dbe2f9164eef0265be7bb6c20c81c95a788997d927e3f"));
        PublicKey publicKey = KeyUtils.publicKeyFromByteSequence(ByteArray.from(
                "302a300506032b657003210081baa2b679aa2c8fb75dbe2f9164eef0265be7bb6c20c81c95a788997d927e3f"));

        AuthenticationKey authenticationKey = AuthenticationKey.fromPublicKey(publicKey);
        AuthenticationKey authenticationKeyTarget = AuthenticationKey
                .fromHexString("c0c19d6b1d48371ea28f0cdc5f74bba7b3f7e8e38f8c8393f281a2f0792a2849");

        long amount = 1;
        int sequenceNumber = 5;

        logger.info("Source account authentication key: {}", authenticationKey);

        logger.info("Sending from {} to {}", AccountAddress.fromAuthenticationKey(authenticationKey),
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(amount * 1000000);
        AccountAddressArgument addressArgument = new AccountAddressArgument(
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        // When you are sending money to an account that does not exist, you need to
        // provide the auth key prefix parameter. You can leave it as an empty byte
        // array if
        // the account exists.
        ByteArrayArgument authkeyPrefixArgument = new ByteArrayArgument(
                authenticationKeyTarget.toByteArray().subseq(0, 16));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(240000)
                .gasUnitPrice(0)
                .senderAccount(AccountAddress.fromAuthenticationKey(authenticationKey))
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferAsBytes())
                        .addArguments(addressArgument, authkeyPrefixArgument, amountArgument)
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorEd25519.builder()
                        .publicKey(dev.jlibra.PublicKey.fromPublicKey(publicKey))
                        .signature(Signature.signTransaction(transaction, privateKey))
                        .build())
                .transaction(transaction)
                .build();

        client.submit(signedTransaction);
    }

}
