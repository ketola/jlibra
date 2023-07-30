package dev.jlibra.example;

import static dev.jlibra.poller.Conditions.transactionFound;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

import java.security.PrivateKey;
import java.security.Security;
import java.time.Instant;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.KeyUtils;
import dev.jlibra.MultiSignaturePublicKey;
import dev.jlibra.PublicKey;
import dev.jlibra.client.DiemClient;
import dev.jlibra.move.Move;
import dev.jlibra.poller.Wait;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.transaction.ChainId;
import dev.jlibra.transaction.ImmutableScript;
import dev.jlibra.transaction.ImmutableSignedTransaction;
import dev.jlibra.transaction.ImmutableTransaction;
import dev.jlibra.transaction.ImmutableTransactionAuthenticatorMultiEd25519;
import dev.jlibra.transaction.Signature;
import dev.jlibra.transaction.SignedTransaction;
import dev.jlibra.transaction.Struct;
import dev.jlibra.transaction.Transaction;
import dev.jlibra.transaction.argument.AccountAddressArgument;
import dev.jlibra.transaction.argument.U64Argument;
import dev.jlibra.transaction.argument.U8VectorArgument;

public class TransferMultisigExample {

    private static final String CURRENCY = "XUS";
    private static final Logger logger = LoggerFactory.getLogger(TransferMultisigExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        PrivateKey privateKey1 = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b6570042204209340bab58bdd0f293dae15a41f8e390eac94cd0f7270120fae25a6c1c9d80a6b81210085c582f99e99edca5417802e108f4cd47c362d352992d616ef30608ee3675955"));
        PublicKey publicKey1 = PublicKey.fromHexString(
                "302a300506032b657003210085c582f99e99edca5417802e108f4cd47c362d352992d616ef30608ee3675955");

        PrivateKey privateKey2 = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b6570042204200f38bbd64e621c6444abfc3c9b07d6d45e1b52ea9f06eb1740d60c412ddd2b388121003f277edec12f11e182cec2f8eecd7411ba636e9d6e4c8e66ff07e289e9cd3dae"));
        PublicKey publicKey2 = PublicKey.fromHexString(
                "302a300506032b65700321003f277edec12f11e182cec2f8eecd7411ba636e9d6e4c8e66ff07e289e9cd3dae");

        AuthenticationKey authenticationKeyTarget = AuthenticationKey
                .fromHexString("c0c19d6b1d48371ea28f0cdc5f74bba7b3f7e8e38f8c8393f281a2f0792a2849");

        MultiSignaturePublicKey multiPubKey = MultiSignaturePublicKey.create(
                Arrays.asList(publicKey1, publicKey2), 2);

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = U64Argument.from(1_000_000);
        AccountAddressArgument addressArgument = AccountAddressArgument
                .from(AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        AuthenticationKey authenticationKey = AuthenticationKey.fromMultiSignaturePublicKey(multiPubKey);
        AccountAddress senderAddress = AccountAddress.fromAuthenticationKey(authenticationKey);
        U8VectorArgument metadataArgument = U8VectorArgument.from(
                ByteArray.from("This is the metadata, you can put anything here!".getBytes(UTF_8)));
        // signature can be used for approved transactions, we are not doing that and
        // can set the signature as an empty byte array
        U8VectorArgument signatureArgument = U8VectorArgument.from(
                ByteArray.from(new byte[0]));

        logger.info("Sender auth key {}, sender address {}", authenticationKey, senderAddress);
        logger.info("Receiver auth key {}, receiver address {}", authenticationKeyTarget,
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        int sequenceNumber = 3;
        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(640000)
                .gasCurrencyCode(CURRENCY)
                .gasUnitPrice(1)
                .sender(senderAddress)
                .expirationTimestampSecs(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferWithMetadata())
                        .typeArguments(asList(Struct.typeTagForCurrency(CURRENCY)))
                        .addArguments(addressArgument, amountArgument, metadataArgument, signatureArgument)
                        .build())
                .chainId(ChainId.TESTING)
                .build();

        Signature signature = Signature.newMultisignature();
        signature = Signature.addSignatureToMultiSignature(signature, 0, transaction, privateKey1);
        signature = Signature.addSignatureToMultiSignature(signature, 1, transaction, privateKey2);

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorMultiEd25519.builder()
                        .publicKey(multiPubKey)
                        .signature(signature)
                        .build())
                .transaction(transaction)
                .build();

        DiemClient client = DiemClient.builder()
                .withUrl("http://localhost:8080")
                .build();

        client.submit(signedTransaction);

        Wait.until(transactionFound(AccountAddress.fromHexString("4fa2be7ad55936c5702e8b7e3fdedb05"),
                sequenceNumber, client));

        logger.info("Receiver account: {}",
                client.getAccount(AccountAddress.fromHexString("b3f7e8e38f8c8393f281a2f0792a2849")));
    }

}
