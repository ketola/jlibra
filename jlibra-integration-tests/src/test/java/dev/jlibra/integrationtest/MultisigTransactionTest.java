package dev.jlibra.integrationtest;

import static dev.jlibra.poller.Conditions.accountHasPositiveBalance;
import static dev.jlibra.poller.Conditions.transactionFound;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.MultiSignaturePublicKey;
import dev.jlibra.PublicKey;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;
import dev.jlibra.faucet.Faucet;
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
import dev.jlibra.transaction.argument.BoolArgument;
import dev.jlibra.transaction.argument.U64Argument;
import dev.jlibra.transaction.argument.U8VectorArgument;

public class MultisigTransactionTest {

    private static final Logger logger = LoggerFactory.getLogger(MultisigTransactionTest.class);

    private static final String CURRENCY = "LBR";

    private LibraClient client;

    @BeforeEach
    public void setUp() {
        Security.addProvider(new BouncyCastleProvider());
        client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();
    }

    @Test
    public void testMultisigTransaction() {
        Faucet faucet = Faucet.builder()
                .build();

        // source account, multisig account with 32 keypairs, 30 threshold
        List<KeyPair> keyPairs = generateKeyPairs(32);
        MultiSignaturePublicKey multiPubKey = MultiSignaturePublicKey.create(
                keyPairs.stream().map(kp -> PublicKey.fromPublicKey(kp.getPublic())).collect(toList()),
                30);

        AuthenticationKey authenticationKey = AuthenticationKey.fromMultiSignaturePublicKey(multiPubKey);
        AccountAddress accountAddress = AccountAddress.fromAuthenticationKey(authenticationKey);
        faucet.mint(authenticationKey, 10 * 1_000_000, CURRENCY);
        Wait.until(accountHasPositiveBalance(accountAddress, client));

        // target account
        KeyPair targetAccount = generateKeyPairs(1).get(0);
        createChildVaspAccount(keyPairs, targetAccount);
        PublicKey publicKeyTarget = PublicKey.fromPublicKey(targetAccount.getPublic());
        AuthenticationKey authenticationKeyTarget = AuthenticationKey.fromPublicKey(publicKeyTarget);

        long transferAmount = 2_000_000;
        // Arguments for the peer to peer transaction
        U64Argument amountArgument = U64Argument.from(transferAmount);
        AccountAddressArgument addressArgument = AccountAddressArgument.from(
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        U8VectorArgument metadataArgument = U8VectorArgument.from(
                ByteArray.from(new byte[0]));
        U8VectorArgument signatureArgument = U8VectorArgument.from(
                ByteArray.from(new byte[0]));

        logger.info("Sender auth key {}, sender address {}", authenticationKey, accountAddress);
        logger.info("Receiver auth key {}, sender address {}", authenticationKeyTarget,
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        int sequenceNumber = 1;
        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(2_000_000)
                .gasCurrencyCode(CURRENCY)
                .gasUnitPrice(1)
                .sender(accountAddress)
                .expirationTimestampSecs(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferWithMetadata())
                        .typeArguments(asList(Struct.typeTagForCurrency(CURRENCY)))
                        .addArguments(addressArgument, amountArgument, metadataArgument, signatureArgument)
                        .build())
                .chainId(ChainId.TESTNET)
                .build();

        Signature signature = createSignature(keyPairs, transaction);

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorMultiEd25519.builder()
                        .publicKey(multiPubKey)
                        .signature(signature)
                        .build())
                .transaction(transaction)
                .build();

        client.submit(signedTransaction);

        Wait.until(transactionFound(accountAddress, sequenceNumber, client));

        Account targetAccountState = client
                .getAccount(AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        assertThat(targetAccountState.balances().get(0).amount(), is(transferAmount));
    }

    private Signature createSignature(List<KeyPair> keyPairs, Transaction transaction) {
        // add 30 signatures, leave out 1st and last signer
        Signature signature = Signature.newMultisignature();
        for (int i = 1; i < 31; i++) {
            signature = Signature.addSignatureToMultiSignature(signature, i, transaction, keyPairs.get(i).getPrivate());
        }
        return signature;
    }

    private KeyPairGenerator getKeyPairGenerator() {
        try {
            return KeyPairGenerator.getInstance("Ed25519", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new LibraRuntimeException("Generate key pair failed", e);
        }
    }

    private void createChildVaspAccount(List<KeyPair> keyPairs, KeyPair childVaspAccountKeyPair) {
        MultiSignaturePublicKey multiPubKey = MultiSignaturePublicKey.create(
                keyPairs.stream().map(kp -> PublicKey.fromPublicKey(kp.getPublic())).collect(toList()),
                30);

        AuthenticationKey childVaspAccountAuthKey = AuthenticationKey
                .fromPublicKey(childVaspAccountKeyPair.getPublic());
        AccountAddress childVaspAccountAddress = AccountAddress.fromAuthenticationKey(childVaspAccountAuthKey);

        logger.info("Child vasp authentication key: {} address: {}", childVaspAccountAuthKey, childVaspAccountAddress);

        AccountAddressArgument childAccountArgument = AccountAddressArgument.from(childVaspAccountAddress);
        U8VectorArgument authKeyPrefixArgument = U8VectorArgument.from(childVaspAccountAuthKey.prefix());
        BoolArgument createAllCurrenciesArgument = BoolArgument.from(false);
        U64Argument initialBalanceArgument = U64Argument.from(0);

        int sequenceNumber = 0;
        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(640000)
                .gasUnitPrice(1)
                .gasCurrencyCode(CURRENCY)
                .sender(AccountAddress
                        .fromAuthenticationKey(AuthenticationKey.fromMultiSignaturePublicKey(multiPubKey)))
                .expirationTimestampSecs(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .typeArguments(asList(Struct.typeTagForCurrency(CURRENCY)))
                        .code(Move.createChildVaspAccount())
                        .addArguments(childAccountArgument, authKeyPrefixArgument, createAllCurrenciesArgument,
                                initialBalanceArgument)
                        .build())
                .chainId(ChainId.TESTNET)
                .build();

        Signature signature = createSignature(keyPairs, transaction);

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorMultiEd25519.builder()
                        .publicKey(multiPubKey)
                        .signature(signature)
                        .build())
                .transaction(transaction)
                .build();

        client.submit(signedTransaction);
        Wait.until(transactionFound(
                AccountAddress.fromAuthenticationKey(AuthenticationKey.fromMultiSignaturePublicKey(multiPubKey)),
                sequenceNumber, client));
    }

    private List<KeyPair> generateKeyPairs(int amount) {
        KeyPairGenerator kpGen = getKeyPairGenerator();
        List<KeyPair> keypairs = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            keypairs.add(kpGen.generateKeyPair());
        }
        return keypairs;
    }
}
