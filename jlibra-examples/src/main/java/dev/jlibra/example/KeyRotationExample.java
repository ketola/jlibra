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

public class KeyRotationExample {

    private static final Logger logger = LogManager.getLogger(KeyRotationExample.class);

    public static void main(String[] args) throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");

        // Create the account by minting some coins to it
        KeyPair keyPairOriginal = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyOriginal = (BCEdDSAPrivateKey) keyPairOriginal.getPrivate();
        BCEdDSAPublicKey publicKeyOriginal = (BCEdDSAPublicKey) keyPairOriginal.getPublic();
        byte[] addressOriginal = KeyUtils.toByteArrayLibraAddress(publicKeyOriginal.getEncoded());
        mint(addressOriginal, (10L * 1_000_000L));

        logger.info("Original Libra address: {}", KeyUtils.toHexStringLibraAddress(publicKeyOriginal.getEncoded()));
        logger.info("Original Public key: {}",
                Hex.toHexString(KeyUtils.stripPublicKeyPrefix(publicKeyOriginal.getEncoded())));
        logger.info("Original Private key: {}", Hex.toHexString(privateKeyOriginal.getEncoded()));
        getAccountState(addressOriginal, admissionControl);

        KeyPair keyPairNew = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyNew = (BCEdDSAPrivateKey) keyPairNew.getPrivate();
        BCEdDSAPublicKey publicKeyNew = (BCEdDSAPublicKey) keyPairNew.getPublic();
        logger.info("New Public key: {}", Hex.toHexString(KeyUtils.stripPublicKeyPrefix(publicKeyNew.getEncoded())));
        logger.info("New Private key: {}", Hex.toHexString(privateKeyNew.getEncoded()));

        KeyPair keyPairNew2 = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeyNew2 = (BCEdDSAPrivateKey) keyPairNew2.getPrivate();
        BCEdDSAPublicKey publicKeyNew2 = (BCEdDSAPublicKey) keyPairNew2.getPublic();
        logger.info("New Public key 2: {}", Hex.toHexString(publicKeyNew2.getEncoded()));
        logger.info("New Private key 2: {}",
                Hex.toHexString(KeyUtils.stripPublicKeyPrefix(privateKeyNew2.getEncoded())));

        SubmitTransactionResult result = rotateAuthenticationKey(privateKeyOriginal, publicKeyOriginal, addressOriginal,
                publicKeyNew, 0, admissionControl);
        logger.info("VM status: {}", result.getVmStatus());

        Thread.sleep(5000);
        mint(addressOriginal, (10L * 1_000_000L));
        getAccountState(addressOriginal, admissionControl);

        result = rotateAuthenticationKey(privateKeyOriginal, publicKeyOriginal, addressOriginal,
                publicKeyNew2, 1, admissionControl);
        logger.info("VM status: {}", result.getVmStatus());

        Thread.sleep(5000);
        getAccountState(addressOriginal, admissionControl);

        result = rotateAuthenticationKey(privateKeyNew, publicKeyNew, addressOriginal,
                publicKeyNew2, 1, admissionControl);
        logger.info("VM status: {}", result.getVmStatus());
        getAccountState(addressOriginal, admissionControl);

        channel.shutdown();
        Thread.sleep(3000); // add sleep to prevent premature closing of channel
    }

    private static SubmitTransactionResult rotateAuthenticationKey(BCEdDSAPrivateKey privateKey,
            BCEdDSAPublicKey publicKey, byte[] address, BCEdDSAPublicKey publicKeyNew,
            int sequenceNumber, AdmissionControl admissionControl) {
        ByteArrayArgument newPublicKeyArgument = new ByteArrayArgument(
                KeyUtils.stripPublicKeyPrefix(publicKeyNew.getEncoded()));

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
            logger.info("Address: {}", Hex.toHexString(accountState.getAccountAddress()));
            logger.info("Received events: {}", accountState.getReceivedEvents().getCount());
            logger.info("Sent events: {}", accountState.getSentEvents().getCount());
            logger.info("Balance (microLibras): {}", accountState.getBalanceInMicroLibras());
            logger.info("Balance (Libras): {}",
                    new BigDecimal(accountState.getBalanceInMicroLibras()).divide(BigDecimal.valueOf(1000000)));
            logger.info("Sequence number: {}", accountState.getSequenceNumber());
            logger.info("Delegated withdrawal capability: {}", accountState.getDelegatedWithdrawalCapability());
        });
    }

}
