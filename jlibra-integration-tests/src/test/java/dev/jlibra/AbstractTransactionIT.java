package dev.jlibra;

import static dev.jlibra.mnemonic.Mnemonic.WORDS;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static org.junit.Assert.assertEquals;

import java.security.Security;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.jlibra.admissioncontrol.AdmissionControl;
import dev.jlibra.admissioncontrol.query.AccountResource;
import dev.jlibra.admissioncontrol.query.ImmutableGetAccountState;
import dev.jlibra.admissioncontrol.query.ImmutableQuery;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Signature;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.admissioncontrol.transaction.result.LibraTransactionException;
import dev.jlibra.admissioncontrol.transaction.result.SubmitTransactionResult;
import dev.jlibra.mnemonic.ChildNumber;
import dev.jlibra.mnemonic.ExtendedPrivKey;
import dev.jlibra.mnemonic.LibraKeyFactory;
import dev.jlibra.mnemonic.Mnemonic;
import dev.jlibra.mnemonic.Seed;
import dev.jlibra.serialization.ByteArray;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 * <p>
 * <ol>
 * <li>Create two key pairs A and B.</li>
 * <li>Mint X libras for account represented by key pair A.</li>
 * <li>Transfer amount Y from A to B and verify the transaction.</li>
 * </ol>
 */
public abstract class AbstractTransactionIT {

    private static final Logger logger = LogManager.getLogger(AbstractTransactionIT.class);

    private static final String TEST_SALT = "Salt, pepper and a dash of sugar.";
    private static final String TESTNET_ADDRESS = "ac.testnet.libra.org";
    private static final int TESTNET_PORT = 8000;

    private ManagedChannel channel;
    private AdmissionControl admissionControl;
    protected ExtendedPrivKey sourceAccount;

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Before
    public void setUp() {
        // source account is fixed
        Mnemonic sourceMnemonic = Mnemonic.fromString(
                "hurry seven priority awful wear jeans antique add fetch sure negative finish suit draft myself chimney spend marine clock furnace draft public erase evidence");
        Seed seed = new Seed(sourceMnemonic, "LIBRA");
        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);
        sourceAccount = libraKeyFactory.privateChild(new ChildNumber(0));

        channel = ManagedChannelBuilder.forAddress(TESTNET_ADDRESS, TESTNET_PORT)
                .usePlaintext()
                .build();

        admissionControl = new AdmissionControl(channel);
    }

    @After
    public void tearDown() {
        channel.shutdown();
    }

    @Test
    public void transferTest() throws Exception {

        // mint something to source account so we don't run out of coins
        mint();

        // destination account is generated
        ExtendedPrivKey destination = generateKey();
        AccountAddress destinationAddress = destination.getAddress();
        long transactionAmount = 1_111;

        // make the transaction
        transfer(destinationAddress, transactionAmount);

        // wait for balance to become visible
        with().pollInterval(fibonacci().with().timeUnit(SECONDS)).await()
                .atMost(10, SECONDS)
                .untilAsserted(() -> {
                    long actual = findBalance(destinationAddress);
                    String errorMessage = format("Account address: %s, expected balance: %d, actual balance: %d",
                            destinationAddress, transactionAmount, actual);
                    assertEquals(errorMessage, actual, transactionAmount);
                });
    }

    private long findBalance(AccountAddress accountAddress) {
        UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(
                ImmutableQuery.builder().accountStateQueries(
                        asList(ImmutableGetAccountState.builder().address(accountAddress).build()))
                        .build());

        long balance = result.getAccountStateQueryResults()
                .stream()
                .filter(accountResource -> accountResource.getAuthenticationKey()
                        .equals(ByteArray.from(accountAddress.toArray())))
                .map(AccountResource::getBalanceInMicroLibras)
                .findFirst()
                .orElse(0L);

        logger.info("Balance for {} is {}", accountAddress.toString(), balance);

        return balance;
    }

    private void transfer(AccountAddress toAddress, long amount) throws LibraTransactionException {

        long sequenceNumber = maybeFindSequenceNumber(admissionControl, sourceAccount.getAddress());

        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(amount);
        AccountAddressArgument addressArgument = new AccountAddressArgument(toAddress);

        Transaction transaction = createTransaction(sequenceNumber, amountArgument, addressArgument);

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(PublicKey.fromPublicKey(sourceAccount.publicKey))
                .transaction(transaction)
                .signature(Signature.signTransaction(transaction, sourceAccount.privateKey))
                .build();

        SubmitTransactionResult result = admissionControl.submitTransaction(signedTransaction);
        System.out.println("Transaction submitted with result: " + result.toString());
    }

    protected abstract Transaction createTransaction(long sequenceNumber, U64Argument amountArgument,
            AccountAddressArgument addressArgument);

    private long maybeFindSequenceNumber(AdmissionControl admissionControl, AccountAddress forAddress) {
        UpdateToLatestLedgerResult result = admissionControl.updateToLatestLedger(
                ImmutableQuery.builder().accountStateQueries(asList(
                        ImmutableGetAccountState.builder().address(forAddress).build())).build());

        return result.getAccountStateQueryResults()
                .stream()
                .filter(accountResource -> accountResource.getAuthenticationKey()
                        .equals(ByteArray.from(forAddress.toArray())))
                .map(AccountResource::getSequenceNumber)
                .findFirst()
                .orElse(0);
    }

    private void mint() {
        long amountInMicroLibras = 1_000_000_000;

        HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                .queryString("amount", amountInMicroLibras)
                .queryString("address", sourceAccount.getAddress().toString())
                .asString();

        with().pollInterval(fibonacci().with().timeUnit(SECONDS)).await()
                .atMost(10, SECONDS)
                .until(() -> findBalance(sourceAccount.getAddress()) > 0);

        assertEquals(200, response.getStatus());
    }

    private ExtendedPrivKey generateKey() {
        String words = IntStream.range(0, 18)
                .map(ignored -> RandomUtils.nextInt(0, WORDS.size()))
                .mapToObj(WORDS::get)
                .collect(Collectors.joining(" "));

        logger.info("Generated seed: {}", words);

        Seed seed = new Seed(Mnemonic.fromString(words), TEST_SALT);

        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);
        return libraKeyFactory.privateChild(new ChildNumber(0));
    }
}
