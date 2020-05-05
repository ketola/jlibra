package dev.jlibra.example;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.time.Instant;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransactionAuthenticatorEd25519;
import dev.jlibra.admissioncontrol.transaction.LbrTypeTag;
import dev.jlibra.admissioncontrol.transaction.Signature;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.client.LibraClient;
import dev.jlibra.move.Move;
import dev.jlibra.serialization.ByteArray;

public class TransferExample {

    private static final Logger logger = LoggerFactory.getLogger(TransferExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b657004220420a758d7ef769f2dd20e083bc49b36f68adba445297e0995387e1e9b820c91dbd28121004106ca3138647f6428b2207b89894ce7e0a2e7cf6353d22f59c22db687508f04"));
        PublicKey publicKey = KeyUtils.publicKeyFromByteSequence(ByteArray.from(
                "302a300506032b65700321004106ca3138647f6428b2207b89894ce7e0a2e7cf6353d22f59c22db687508f04"));

        AuthenticationKey authenticationKey = AuthenticationKey.fromPublicKey(publicKey);

        // If the account already exists, then the authentication key of the target
        // account is not required and the account address would be enough
        AuthenticationKey authenticationKeyTarget = AuthenticationKey
                .fromHexString("acb53e7a4b1e0cd77a4c08043191a2308a0461f91654bb308638907907e348cc");

        long amount = 1;
        int sequenceNumber = 11;

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
        ByteArrayArgument authkeyPrefixArgument = new ByteArrayArgument(authenticationKeyTarget.prefix());

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(640000)
                .gasUnitPrice(1)
                .senderAccount(AccountAddress.fromAuthenticationKey(authenticationKey))
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .typeArguments(Arrays.asList(new LbrTypeTag()))
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
