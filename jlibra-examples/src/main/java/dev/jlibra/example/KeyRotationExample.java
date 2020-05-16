package dev.jlibra.example;

import static dev.jlibra.poller.Conditions.accountExists;
import static dev.jlibra.poller.Conditions.transactionFound;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.time.Instant;
import java.util.ArrayList;

import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransactionAuthenticatorEd25519;
import dev.jlibra.admissioncontrol.transaction.Signature;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;
import dev.jlibra.example.util.ExampleUtils;
import dev.jlibra.move.Move;
import dev.jlibra.poller.Wait;

public class KeyRotationExample {

    private static final Logger logger = LoggerFactory.getLogger(KeyRotationExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        /*
         * Create a key pair, calculate the libra address and add some coins to the
         * account
         */
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");
        KeyPair keyPairOriginal = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyOriginal = (BCEdDSAPrivateKey) keyPairOriginal.getPrivate();
        BCEdDSAPublicKey publicKeyOriginal = (BCEdDSAPublicKey) keyPairOriginal.getPublic();
        AuthenticationKey authenticationKeyOriginal = AuthenticationKey.fromPublicKey(publicKeyOriginal);
        AccountAddress addressOriginal = AccountAddress.fromAuthenticationKey(authenticationKeyOriginal);

        logger.info("Account address: {}", addressOriginal.toString());
        logger.info("Authentication key: {}", authenticationKeyOriginal.toString());
        ExampleUtils.mint(AuthenticationKey.fromPublicKey(publicKeyOriginal), 10L * 1_000_000L);

        Wait.until(accountExists(addressOriginal, client));

        /*
         * Get the account state to verify that the account exists and the coins were
         * transferred. Notice, that at this point the address == authentication key.
         */
        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Get the account state for the new account");
        getAccountState(addressOriginal, client);
        logger.info(
                "-----------------------------------------------------------------------------------------------\n");
        /*
         * Create a new key pair. This keypair will be changed to be the signing keys of
         * the account created earlier
         */
        KeyPair keyPairNew = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyNew = (BCEdDSAPrivateKey) keyPairNew.getPrivate();
        BCEdDSAPublicKey publicKeyNew = (BCEdDSAPublicKey) keyPairNew.getPublic();

        /*
         * Send the transaction for changing the signing keys
         */
        logger.info("Change the signing keys..");
        rotateAuthenticationKey(privateKeyOriginal, publicKeyOriginal, addressOriginal,
                publicKeyNew, 0, client);

        Wait.until(transactionFound(addressOriginal, 0, client));
        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Get the account state for the account");
        getAccountState(addressOriginal, client);
        logger.info(
                "-----------------------------------------------------------------------------------------------\n");

        /*
         * In this step a new key pair is created and an attempt is made to change these
         * to be the new signing keys. This should fail because we are using the
         * original signing keys which should not work anymore because the keys were
         * changed.
         */
        KeyPair keyPairNew2 = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyNew2 = (BCEdDSAPrivateKey) keyPairNew2.getPrivate();
        BCEdDSAPublicKey publicKeyNew2 = (BCEdDSAPublicKey) keyPairNew2.getPublic();
        logger.info("Change the signing keys..");
        try {
            rotateAuthenticationKey(privateKeyOriginal, publicKeyOriginal, addressOriginal,
                    publicKeyNew2, 1, client);

        } catch (LibraRuntimeException e) {
            logger.error(e.getMessage());
            logger.error("This failed because the the original keys cannot be used anymore");
        }

        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Get the account state for the account");
        getAccountState(addressOriginal, client);
        logger.info(
                "-----------------------------------------------------------------------------------------------\n");

        /*
         * Now a new attempt is done using the correct keys and this time the
         * transaction should go through.
         */
        logger.info("Change the signing keys..");
        rotateAuthenticationKey(privateKeyNew, publicKeyNew, addressOriginal,
                publicKeyNew2, 1, client);
        logger.info("This succeeded because now the updated keys were used.");
        Wait.until(transactionFound(addressOriginal, 1, client));

        /*
         * Get the account state to verify that the authentication key was changed.
         */
        logger.info("-----------------------------------------------------------------------------------------------");
        getAccountState(addressOriginal, client);
        logger.info("-----------------------------------------------------------------------------------------------");
    }

    private static void rotateAuthenticationKey(BCEdDSAPrivateKey privateKey,
            BCEdDSAPublicKey publicKey, AccountAddress address, BCEdDSAPublicKey publicKeyNew,
            int sequenceNumber, LibraClient client) {

        ByteArrayArgument newAuthenticationKeyArgument = new ByteArrayArgument(
                AuthenticationKey.fromPublicKey(publicKeyNew));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(640000)
                .gasUnitPrice(1)
                .senderAccount(address)
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .typeArguments(new ArrayList<>())
                        .code(Move.rotateAuthenticationKeyAsBytes())
                        .addArguments(newAuthenticationKeyArgument)
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

    private static void getAccountState(AccountAddress accountAddress, LibraClient libraClient) {
        Account account = libraClient.getAccountState(accountAddress);

        logger.info(
                "Account authentication key: {}, Balance (Libras): {}",
                account.authenticationKey(),
                new BigDecimal(account.balances().get(0).amount()).divide(BigDecimal.valueOf(1000000)));
    }
}
