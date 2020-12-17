package dev.jlibra.example;

import static dev.jlibra.poller.Conditions.transactionFound;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

import java.security.PrivateKey;
import java.security.Security;
import java.time.Instant;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.KeyUtils;
import dev.jlibra.PublicKey;
import dev.jlibra.client.DiemClient;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.transaction.PeerToPeerWithMetadataScript;
import dev.jlibra.client.views.transaction.UserTransaction;
import dev.jlibra.move.Move;
import dev.jlibra.poller.Wait;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.transaction.ChainId;
import dev.jlibra.transaction.ImmutableScript;
import dev.jlibra.transaction.ImmutableSignedTransaction;
import dev.jlibra.transaction.ImmutableTransaction;
import dev.jlibra.transaction.ImmutableTransactionAuthenticatorEd25519;
import dev.jlibra.transaction.Signature;
import dev.jlibra.transaction.SignedTransaction;
import dev.jlibra.transaction.Struct;
import dev.jlibra.transaction.Transaction;
import dev.jlibra.transaction.argument.AccountAddressArgument;
import dev.jlibra.transaction.argument.U64Argument;
import dev.jlibra.transaction.argument.U8VectorArgument;

/**
 * Both accounts have to exist before making the transaction. Use
 * GenerateKeysExample and MintExample to create accounts with some money in
 * them.
 * 
 */
public class TransferExample {

    private static final String CURRENCY = "XUS";
    private static final Logger logger = LoggerFactory.getLogger(TransferExample.class);

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        DiemClient client = DiemClient.builder()
                .withUrl("https://testnet.diem.com/v1")
                .build();

        PrivateKey privateKey = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b657004220420aeff20e881cd4c7f32b23b74ab6c9ffce5b2764047d141b1c84e47f6c3b656d08121006495a72dd63c25c3173eeaa9b5f5c03050a669b4845db67f779f8d8839911562"));
        PublicKey publicKey = PublicKey.fromHexString(
                "302a300506032b65700321006495a72dd63c25c3173eeaa9b5f5c03050a669b4845db67f779f8d8839911562");

        AuthenticationKey authenticationKey = AuthenticationKey.fromPublicKey(publicKey);
        AccountAddress sourceAccount = AccountAddress.fromAuthenticationKey(authenticationKey);
        logger.info("Source account authentication key: {}, address: {}", authenticationKey, sourceAccount);
        Account accountState = client.getAccount(sourceAccount);

        // If the account already exists, then the authentication key of the target
        // account is not required and the account address would be enough
        AuthenticationKey authenticationKeyTarget = AuthenticationKey
                .fromHexString("37b1b993c4d932b6830332d01f3cde4afe7f9fc36a7b207ef8960d2b81259180");

        long amount = 1;
        long sequenceNumber = accountState.sequenceNumber();

        logger.info("Sending from {} to {}", AccountAddress.fromAuthenticationKey(authenticationKey),
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = U64Argument.from(amount * 1000000);
        AccountAddressArgument addressArgument = AccountAddressArgument.from(
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));
        U8VectorArgument metadataArgument = U8VectorArgument.from(
                ByteArray.from("This is the metadata, you can put anything here!".getBytes(UTF_8)));
        // signature can be used for approved transactions, we are not doing that and
        // can set the signature as an empty byte array
        U8VectorArgument signatureArgument = U8VectorArgument.from(
                ByteArray.from(new byte[0]));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(1640000)
                .gasCurrencyCode(CURRENCY)
                .gasUnitPrice(1)
                .sender(sourceAccount)
                .expirationTimestampSecs(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .typeArguments(asList(Struct.typeTagForCurrency(CURRENCY)))
                        .code(Move.peerToPeerTransferWithMetadata())
                        .addArguments(addressArgument, amountArgument, metadataArgument,
                                signatureArgument)
                        .build())
                .chainId(ChainId.TESTNET)
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorEd25519.builder()
                        .publicKey(publicKey)
                        .signature(Signature.signTransaction(transaction, privateKey))
                        .build())
                .transaction(transaction)
                .build();

        client.submit(signedTransaction);

        // get the transaction and read the metadata
        Wait.until(transactionFound(AccountAddress.fromAuthenticationKey(authenticationKey), sequenceNumber,
                client));
        UserTransaction t = (UserTransaction) client.getAccountTransaction(sourceAccount, sequenceNumber, true)
                .transaction();
        PeerToPeerWithMetadataScript script = (PeerToPeerWithMetadataScript) t.script();

        logger.info("Metadata: {}", new String(Hex.decode(script.metadata())));
    }

}
