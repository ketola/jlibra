package dev.jlibra.example;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;

import dev.jlibra.AccountAddress;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountTransactionBySequenceNumber;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignature;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.admissioncontrol.transaction.result.SubmitTransactionResult;
import dev.jlibra.move.Move;
import dev.jlibra.serialization.ByteSequence;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * A transaction can also contain metadata that is preserved with the
 * transaction. This could contain t. ex. a reference number to an order.
 * 
 * This is made possible by a function in the move script that accepts the
 * metadata as a parameter (see
 * https://github.com/libra/libra/blob/master/language/stdlib/transaction_scripts/peer_to_peer_transfer_with_metadata.mvir)
 * The data is stored in the events that are emitted from the transaction.
 * 
 * This example creates a transaction with metadata and reads the information
 * then from the api.
 */
public class TransactionWithMetadataExample {

    private static final Logger logger = LogManager.getLogger(TransactionWithMetadataExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");

        /*
         * Create a target and a source account and add 10 Libras to the source account
         */
        KeyPair keyPairSource = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeySource = (BCEdDSAPrivateKey) keyPairSource.getPrivate();
        BCEdDSAPublicKey publicKeySource = (BCEdDSAPublicKey) keyPairSource.getPublic();
        AccountAddress source = AccountAddress.ofPublicKey(publicKeySource);
        ExampleUtils.mint(source, 10L * 1_000_000L);

        KeyPair keyPairTarget = kpGen.generateKeyPair();
        BCEdDSAPublicKey publicKeyTarget = (BCEdDSAPublicKey) keyPairTarget.getPublic();
        AccountAddress target = AccountAddress.ofPublicKey(publicKeyTarget);

        // sleep for 1 sec to make sure the minted money is available in the account.
        // Sometimes the faucet api is working slowly and you might need to increase the
        // time.
        Thread.sleep(1000);

        logger.info("Sending from {} to {}", source, target);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControl admissionControl = new AdmissionControl(channel);

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(1_000_000);
        AccountAddressArgument addressArgument = new AccountAddressArgument(target.getByteSequence());
        ByteArrayArgument metadata = new ByteArrayArgument(
                ByteSequence
                        .from("Logic will get you from A to Z; imagination will get you everywhere.".getBytes(UTF_8)));

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(0)
                .maxGasAmount(140000)
                .gasUnitPrice(0)
                .senderAccount(AccountAddress.ofPublicKey(publicKeySource))
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferWithMetadataAsBytes())
                        .addArguments(addressArgument, amountArgument, metadata)
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(publicKeySource)
                .transaction(transaction)
                .signature(ImmutableSignature.builder()
                        .privateKey(privateKeySource)
                        .transaction(transaction)
                        .build())
                .build();

        SubmitTransactionResult result = admissionControl.submitTransaction(signedTransaction);

        logger.info("Transaction sent. Result: {}", result);

        Thread.sleep(2000);

        logger.info("Read the transaction information..");

        UpdateToLatestLedgerResult queryResult = admissionControl.updateToLatestLedger(ImmutableQuery.builder()
                .accountTransactionBySequenceNumberQueries(
                        asList(ImmutableGetAccountTransactionBySequenceNumber.builder()
                                .accountAddress(AccountAddress.ofByteSequence(source.getByteSequence()))
                                .sequenceNumber(0)
                                .build()))
                .build());

        queryResult.getAccountTransactionsBySequenceNumber().forEach(tx -> tx.getEvents()
                .forEach(e -> logger.info("{}: Sequence number: {}, Amount: {}, Metadata: {}",
                        e.getAccountAddress(), e.getSequenceNumber(), e.getAmount(),
                        new String(e.getMetadata().get().toArray(), UTF_8))));
    }

}
