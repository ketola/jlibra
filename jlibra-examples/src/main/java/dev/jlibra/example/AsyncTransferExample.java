package dev.jlibra.example;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;

import dev.jlibra.AccountAddress;
import dev.jlibra.PublicKey;
import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.Signature;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.admissioncontrol.transaction.result.SubmitTransactionResult;
import dev.jlibra.move.Move;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Demonstrates the use of the async methods in AdmissionControl.
 * 
 * Creates a source and a target account, creates 10 transactions and submits
 * them. Finally retrieves the account states after the transfers have
 * completed.
 */
public class AsyncTransferExample {

    private static final Logger logger = LogManager.getLogger(AsyncTransferExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");

        /*
         * Create a target and a source account and add 20 Libras to the source account
         */
        KeyPair keyPairSource = kpGen.generateKeyPair();
        BCEdDSAPrivateKey privateKeySource = (BCEdDSAPrivateKey) keyPairSource.getPrivate();
        BCEdDSAPublicKey publicKeySource = (BCEdDSAPublicKey) keyPairSource.getPublic();
        AccountAddress source = AccountAddress.fromPublicKey(publicKeySource);
        ExampleUtils.mint(source, 20L * 1_000_000L);

        KeyPair keyPairTarget = kpGen.generateKeyPair();
        BCEdDSAPublicKey publicKeyTarget = (BCEdDSAPublicKey) keyPairTarget.getPublic();
        AccountAddress target = AccountAddress.fromPublicKey(publicKeyTarget);

        // sleep for 1 sec to make sure the minted money is available in the account.
        // Sometimes the faucet api is working slowly and you might need to increase the
        // time.
        Thread.sleep(1000);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();
        AdmissionControl admissionControl = new AdmissionControl(channel);

        List<CompletableFuture<SubmitTransactionResult>> transactions = new ArrayList<>();

        logger.info("Start creating transactions..");
        for (int i = 0; i < 10; i++) {
            U64Argument amountArgument = new U64Argument(1_000_000);
            AccountAddressArgument addressArgument = new AccountAddressArgument(
                    target);
            Transaction transaction = ImmutableTransaction.builder()
                    .sequenceNumber(i)
                    .maxGasAmount(140000)
                    .gasUnitPrice(0)
                    .senderAccount(AccountAddress.fromPublicKey(publicKeySource))
                    .expirationTime(Instant.now().getEpochSecond() + 60)
                    .payload(ImmutableScript.builder()
                            .code(Move.peerToPeerTransferAsBytes())
                            .addArguments(addressArgument, amountArgument)
                            .build())
                    .build();
            SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                    .publicKey(PublicKey.ofPublicKey(publicKeySource))
                    .transaction(transaction)
                    .signature(Signature.signTransaction(transaction, privateKeySource))
                    .build();
            transactions.add(admissionControl
                    .asyncSubmitTransaction(signedTransaction));
            logger.info("Created transaction {}, sending 1 libra from {} to {}", i, source,
                    target);
        }

        logger.info("All transactions created. Wait for them to be accepted..");
        long time = System.currentTimeMillis();
        CompletableFuture.allOf(transactions.toArray(new CompletableFuture[transactions.size()])).get();
        logger.info("Transactions accepted in {} ms, get account states (both accounts should have 10 Libras): ",
                System.currentTimeMillis() - time);

        // sleep to make sure the transactions have been executed (submitting them
        // successfully only means they are accepted for execution). The correct way
        // would be to poll the api to make sure the transactions have been executed.
        Thread.sleep(2000);

        ImmutableQuery accountStatesQuery = ImmutableQuery.builder()
                .accountStateQueries(asList(
                        ImmutableGetAccountState.builder()
                                .address(target)
                                .build(),
                        ImmutableGetAccountState.builder()
                                .address(source)
                                .build()))
                .build();

        admissionControl.asyncUpdateToLatestLedger(accountStatesQuery)
                .thenApply(UpdateToLatestLedgerResult::getAccountStateQueryResults)
                .thenAccept(resources -> resources.forEach(accountResource -> {
                    logger.info("-------------------------------------------------------------------");
                    logger.info("Authentication key: {}", accountResource.getAuthenticationKey());
                    logger.info("Received events: {}", accountResource.getReceivedEvents().getCount());
                    logger.info("Sent events: {}", accountResource.getSentEvents().getCount());
                    logger.info("Balance (microLibras): {}", accountResource.getBalanceInMicroLibras());
                    logger.info("Balance (Libras): {}", new BigDecimal(accountResource.getBalanceInMicroLibras())
                            .divide(BigDecimal.valueOf(1_000_000)));
                    logger.info("Sequence number: {}", accountResource.getSequenceNumber());
                }));

        Thread.sleep(3000); // add sleep to prevent premature closing of channel
        channel.shutdown();
    }
}
