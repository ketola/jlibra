package dev.jlibra.example;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.util.encoders.Hex;

import com.google.protobuf.ByteString;

import dev.jlibra.AccountAddress;
import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableProgram;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.result.LibraTransactionException;
import dev.jlibra.admissioncontrol.transaction.result.LibraVirtualMachineException;
import dev.jlibra.admissioncontrol.transaction.result.SubmitTransactionResult;
import dev.jlibra.move.Move;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/*-
 * This is a bit more complicated example demonstrating the key rotation feature
 * of Libra.
 * 
 * With the key rotation one can change the signing keys of a Libra account to
 * a new key pair while keeping the account address.
 * 
 * The steps in this example are: 
 * 1. Create a key pair and mint some coins to this new account
 * 2. Create another key pair and create a transaction to change them to be the new signing keys of the account 
 * 3. Verify that the original keys do not work anymore 
 * 4. Verify that the new signing keys work
 * 
 */
public class KeyRotationExample {

    private static final Logger logger = LogManager.getLogger(KeyRotationExample.class);

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();
        AdmissionControl admissionControl = new AdmissionControl(channel);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        /*
         * Create a key pair, calculate the libra address and add some coins to the
         * account
         */
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");
        KeyPair keyPairOriginal = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyOriginal = (BCEdDSAPrivateKey) keyPairOriginal.getPrivate();
        BCEdDSAPublicKey publicKeyOriginal = (BCEdDSAPublicKey) keyPairOriginal.getPublic();
        AccountAddress addressOriginal = AccountAddress.ofPublicKey(publicKeyOriginal);
        logger.info("Account address: {}", addressOriginal.asHexString());
        mint(addressOriginal, 10L * 1_000_000L);
        Thread.sleep(500);

        /*
         * Get the account state to verify that the account exists and the coins were
         * transferred. Notice, that at this point the address == authentication key.
         */
        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Get the account state for the new account");
        getAccountState(addressOriginal, admissionControl);
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
        SubmitTransactionResult result = rotateAuthenticationKey(privateKeyOriginal, publicKeyOriginal, addressOriginal,
                publicKeyNew, 0, admissionControl);
        logger.info("Result: {}", result);

        /*
         * Add some coins to the account to verify that the address is still the same
         * but the authentication key has changed.
         */
        mint(addressOriginal, 10L * 1_000_000L);
        Thread.sleep(500);
        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Get the account state for the account");
        getAccountState(addressOriginal, admissionControl);
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
            result = rotateAuthenticationKey(privateKeyOriginal, publicKeyOriginal, addressOriginal,
                    publicKeyNew2, 1, admissionControl);

        } catch (LibraVirtualMachineException e) {
            logger.error(e.getMessage());
            logger.error("This failed because the the original keys cannot be used anymore");
        }

        /*
         * Now a new attempt is done using the correct keys and this time the
         * transaction should go through.
         */
        logger.info("Change the signing keys..");
        result = rotateAuthenticationKey(privateKeyNew, publicKeyNew, addressOriginal,
                publicKeyNew2, 1, admissionControl);
        logger.info("Result: {}", result);
        logger.info("This succeeded because now the updated keys were used.");
        Thread.sleep(500);

        /*
         * Get the account state to verify that the authentication key was changed.
         */
        logger.info("-----------------------------------------------------------------------------------------------");
        getAccountState(addressOriginal, admissionControl);
        logger.info("-----------------------------------------------------------------------------------------------");

        channel.shutdown();
        Thread.sleep(3000); // add sleep to prevent premature closing of channel
    }

    private static SubmitTransactionResult rotateAuthenticationKey(BCEdDSAPrivateKey privateKey,
            BCEdDSAPublicKey publicKey, AccountAddress address, BCEdDSAPublicKey publicKeyNew,
            int sequenceNumber, AdmissionControl admissionControl) throws LibraTransactionException {

        ByteArrayArgument newPublicKeyArgument = new ByteArrayArgument(
                KeyUtils.toByteArrayLibraAddress(publicKeyNew.getEncoded()));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(160000)
                .gasUnitPrice(1)
                .senderAccount(address)
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .program(
                        ImmutableProgram.builder()
                                .code(ByteString.copyFrom(Move.rotateAuthenticationKeyAsBytes()))
                                .addArguments(newPublicKeyArgument)
                                .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(publicKey)
                .transaction(transaction)
                .privateKey(privateKey)
                .build();

        return admissionControl.submitTransaction(signedTransaction);
    }

    private static void mint(AccountAddress address, long amountInMicroLibras) {
        HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                .queryString("amount", amountInMicroLibras)
                .queryString("address", address.asHexString())
                .asString();

        if (response.getStatus() != 200) {
            throw new IllegalStateException(
                    format("Error in minting %d Libra for address %s", amountInMicroLibras, address));
        }
    }

    private static void getAccountState(AccountAddress accountAddress, AdmissionControl admissionControl) {
        UpdateToLatestLedgerResult result = admissionControl
                .updateToLatestLedger(ImmutableQuery.builder()
                        .accountStateQueries(asList(ImmutableGetAccountState.builder()
                                .address(accountAddress)
                                .build()))
                        .build());

        result.getAccountResources().forEach(accountResource -> logger.info(
                "Account authentication key: {}, Balance (Libras): {}",
                Hex.toHexString(accountResource.getAuthenticationKey()),
                new BigDecimal(accountResource.getBalanceInMicroLibras()).divide(BigDecimal.valueOf(1000000))));
    }
}
