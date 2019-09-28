package dev.jlibra.example;

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

import dev.jlibra.KeyUtils;
import dev.jlibra.LibraHelper;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableProgram;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.SubmitTransactionResult;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.move.Move;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/*-
 * This is bit more complicated example demonstrating the key rotation feature
 * of Libra.
 * 
 * With the key rotation one can change the signing keys of the Libra account to
 * a new key pair.
 * 
 * The steps in this example are: 
 * 1. Create a key pair and mint some coins to this new account
 * 2. Create another key pair and create a transaction to change them to be the new signing keys of the account 
 * 3. Verify that the original key do not work anymore 
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
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");

        logger.info("Create the account with some coins. The signing keys of this account will be later changed...\n");
        KeyPair keyPairOriginal = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyOriginal = (BCEdDSAPrivateKey) keyPairOriginal.getPrivate();
        BCEdDSAPublicKey publicKeyOriginal = (BCEdDSAPublicKey) keyPairOriginal.getPublic();
        byte[] addressOriginal = KeyUtils.toByteArrayLibraAddress(publicKeyOriginal.getEncoded());
        mint(addressOriginal, 10L * 1_000_000L);
        logger.info("Here are the original signing keys of the account:");
        logger.info("Original Libra address: {}", KeyUtils.toHexStringLibraAddress(publicKeyOriginal.getEncoded()));
        logger.info("Original Public key: {}",
                Hex.toHexString(KeyUtils.stripPublicKeyPrefix(publicKeyOriginal.getEncoded())));
        logger.info("Original Private key: {}", Hex.toHexString(privateKeyOriginal.getEncoded()));

        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Get the account state for the account");
        getAccountState(addressOriginal, admissionControl);
        logger.info(
                "-----------------------------------------------------------------------------------------------\n");

        logger.info("Create the new signing keys for the account...");
        KeyPair keyPairNew = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyNew = (BCEdDSAPrivateKey) keyPairNew.getPrivate();
        BCEdDSAPublicKey publicKeyNew = (BCEdDSAPublicKey) keyPairNew.getPublic();
        logger.info("New Public key: {}", Hex.toHexString(KeyUtils.stripPublicKeyPrefix(publicKeyNew.getEncoded())));
        logger.info("New Private key: {}", Hex.toHexString(privateKeyNew.getEncoded()));

        logger.info("Update the new public key for the account..");
        SubmitTransactionResult result = rotateAuthenticationKey(privateKeyOriginal, publicKeyOriginal, addressOriginal,
                publicKeyNew, 0, admissionControl);
        logger.info("VM status: {}", result.getVmStatus());
        logger.info(
                "Mint some more coins for the account using the address created in the first step (this is done to demonstrate that the account address is not changed in this process)...");
        mint(addressOriginal, 10L * 1_000_000L);

        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Get the account state for the account");
        getAccountState(addressOriginal, admissionControl);
        logger.info(
                "-----------------------------------------------------------------------------------------------\n");

        logger.info(
                "Create a third set of signing keys and try to update them to the account using the keys created in the beginning..");
        KeyPair keyPairNew2 = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyNew2 = (BCEdDSAPrivateKey) keyPairNew2.getPrivate();
        BCEdDSAPublicKey publicKeyNew2 = (BCEdDSAPublicKey) keyPairNew2.getPublic();
        logger.info("New Public key 2: {}", Hex.toHexString(publicKeyNew2.getEncoded()));
        logger.info("New Private key 2: {}",
                Hex.toHexString(KeyUtils.stripPublicKeyPrefix(privateKeyNew2.getEncoded())));

        result = rotateAuthenticationKey(privateKeyOriginal, publicKeyOriginal, addressOriginal,
                publicKeyNew2, 1, admissionControl);
        logger.info("VM status: {}", result.getVmStatus());
        logger.info("This failed because the the original keys cannot be used anymore");
        logger.info(
                "-----------------------------------------------------------------------------------------------\n");

        logger.info("Try to update the signing keys using the current key");
        result = rotateAuthenticationKey(privateKeyNew, publicKeyNew, addressOriginal,
                publicKeyNew2, 1, admissionControl);
        logger.info("VM status: {}", result.getVmStatus());
        logger.info("This succeeded because now the updated keys were used.");

        logger.info("-----------------------------------------------------------------------------------------------");
        logger.info("Get the account state for the account");
        getAccountState(addressOriginal, admissionControl);
        logger.info("-----------------------------------------------------------------------------------------------");

        channel.shutdown();
        Thread.sleep(3000); // add sleep to prevent premature closing of channel
    }

    private static SubmitTransactionResult rotateAuthenticationKey(BCEdDSAPrivateKey privateKey,
            BCEdDSAPublicKey publicKey, byte[] address, BCEdDSAPublicKey publicKeyNew,
            int sequenceNumber, AdmissionControl admissionControl) {

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
                .publicKey(KeyUtils.stripPublicKeyPrefix(publicKey.getEncoded()))
                .transaction(transaction)
                .signature(LibraHelper.signTransaction(transaction, privateKey))
                .build();

        SubmitTransactionResult result = admissionControl.submitTransaction(signedTransaction);
        return result;
    }

    private static void mint(byte[] address, long amountInMicroLibras) {
        HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                .queryString("amount", amountInMicroLibras)
                .queryString("address", Hex.toHexString(address))
                .asString();

        if (response.getStatus() != 200) {
            throw new IllegalStateException(
                    String.format("Error in minting %d Libra for address %s", amountInMicroLibras, address));
        }
    }

    private static void getAccountState(byte[] address, AdmissionControl admissionControl) {
        UpdateToLatestLedgerResult result = admissionControl
                .updateToLatestLedger(ImmutableQuery.builder()
                        .addAccountStateQueries(ImmutableGetAccountState.builder()
                                .address(address)
                                .build())
                        .build());

        result.getAccountStates().forEach(accountState -> {
            logger.info("Account authentication key: {}, Balance (Libras): {}",
                    Hex.toHexString(accountState.getAuthenticationKey()),
                    new BigDecimal(accountState.getBalanceInMicroLibras()).divide(BigDecimal.valueOf(1000000)));
        });
    }

}
