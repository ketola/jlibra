package dev.jlibra.example;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.PrivateKey;
import java.security.Security;
import java.time.Instant;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.KeyUtils;
import dev.jlibra.PublicKey;
import dev.jlibra.client.LibraClient;
import dev.jlibra.move.Move;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.transaction.ImmutableScript;
import dev.jlibra.transaction.ImmutableSignedTransaction;
import dev.jlibra.transaction.ImmutableTransaction;
import dev.jlibra.transaction.ImmutableTransactionAuthenticatorEd25519;
import dev.jlibra.transaction.LbrTypeTag;
import dev.jlibra.transaction.Signature;
import dev.jlibra.transaction.SignedTransaction;
import dev.jlibra.transaction.Transaction;
import dev.jlibra.transaction.argument.AccountAddressArgument;
import dev.jlibra.transaction.argument.U64Argument;
import dev.jlibra.transaction.argument.U8VectorArgument;

public class TransferExample {

    private static final Logger logger = LoggerFactory.getLogger(TransferExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b657004220420a758d7ef769f2dd20e083bc49b36f68adba445297e0995387e1e9b820c91dbd28121004106ca3138647f6428b2207b89894ce7e0a2e7cf6353d22f59c22db687508f04"));
        PublicKey publicKey = PublicKey.fromHexString(
                "302a300506032b65700321004106ca3138647f6428b2207b89894ce7e0a2e7cf6353d22f59c22db687508f04");

        AuthenticationKey authenticationKey = AuthenticationKey.fromPublicKey(publicKey);

        // If the account already exists, then the authentication key of the target
        // account is not required and the account address would be enough
        AuthenticationKey authenticationKeyTarget = AuthenticationKey
                .fromHexString("acb53e7a4b1e0cd77a4c08043191a2308a0461f91654bb308638907907e348cc");

        long amount = 1;
        int sequenceNumber = 0;

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

        U8VectorArgument metadataArgument = new U8VectorArgument(
                ByteArray.from("This is the metadata, you can put anything here!".getBytes(UTF_8)));
        // signature can be used for approved transactions, we are not doing that and
        // can set the signature as an empty byte array
        U8VectorArgument signatureArgument = new U8VectorArgument(
                ByteArray.from(new byte[0]));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(640000)
                .gasUnitPrice(1)
                .gasCurrencyCode("LBR")
                .senderAccount(AccountAddress.fromAuthenticationKey(authenticationKey))
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .typeArguments(Arrays.asList(new LbrTypeTag()))
                        .code(Move.peerToPeerTransferWithMetadata())
                        .addArguments(addressArgument, amountArgument)
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorEd25519.builder()
                        .publicKey(publicKey)
                        .signature(Signature.signTransaction(transaction, privateKey))
                        .build())
                .transaction(transaction)
                .build();

        client.submit(signedTransaction);
    }

}
