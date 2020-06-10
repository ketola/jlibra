package dev.jlibra.example;

import static dev.jlibra.poller.Conditions.transactionFound;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.PrivateKey;
import java.security.Security;
import java.time.Instant;
import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.KeyUtils;
import dev.jlibra.PublicKey;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.PeerToPeerTransactionScript;
import dev.jlibra.client.views.UserTransaction;
import dev.jlibra.move.Move;
import dev.jlibra.poller.Wait;
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

public class TransferWithMetadataExample {

    private static final Logger logger = LoggerFactory.getLogger(TransferWithMetadataExample.class);

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        PrivateKey privateKey = KeyUtils.privateKeyFromByteSequence(ByteArray.from(
                "3051020101300506032b6570042204208096fec0a03f968bbece0c717525dded07a4bb123827cf1f8df48920f6def2758121001a9115b2b15e182dc94d8abc15404cb1dbe48211192ecb6c8fca00c369dd1969"));
        PublicKey publicKey = PublicKey.fromHexString(
                "302a300506032b65700321001a9115b2b15e182dc94d8abc15404cb1dbe48211192ecb6c8fca00c369dd1969");

        AuthenticationKey authenticationKey = AuthenticationKey.fromPublicKey(publicKey);

        AccountAddress sourceAccount = AccountAddress.fromAuthenticationKey(authenticationKey);
        Account accountState = client.getAccountState(sourceAccount);

        // If the account already exists, then the authentication key of the target
        // account is not required and the account address would be enough
        AuthenticationKey authenticationKeyTarget = AuthenticationKey
                .fromHexString("f792ee6e15298b234bfcef1d6d00c6c6fc4c85260cdccd2ee25f217da715e5dc");

        long amount = 1;
        long sequenceNumber = accountState.sequenceNumber();

        logger.info("Source account authentication key: {}", authenticationKey);

        logger.info("Sending from {} to {}", AccountAddress.fromAuthenticationKey(authenticationKey),
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

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

        // When you are sending money to an account that does not exist, you need to
        // provide the auth key prefix parameter. You can leave it as an empty byte
        // array if
        // the account exists.
        U8VectorArgument authkeyPrefixArgument = new U8VectorArgument(authenticationKeyTarget.prefix());

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(1640000)
                .gasCurrencyCode("LBR")
                .gasUnitPrice(1)
                .senderAccount(sourceAccount)
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .typeArguments(Arrays.asList(new LbrTypeTag()))
                        .code(Move.peerToPeerTransferWithMetadata())
                        .addArguments(addressArgument, authkeyPrefixArgument, amountArgument, metadataArgument,
                                signatureArgument)
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

        // get the transaction and read the metadata
        Wait.until(transactionFound(AccountAddress.fromAuthenticationKey(authenticationKey), sequenceNumber,
                client));
        UserTransaction t = (UserTransaction) client.getAccountTransaction(sourceAccount, sequenceNumber, true)
                .transaction();
        PeerToPeerTransactionScript script = (PeerToPeerTransactionScript) t.script();

        logger.info("Metadata: {}", new String(Hex.decode(script.metadata())));
    }

}
