package dev.jlibra.integrationtest;

import static dev.jlibra.poller.Conditions.accountExists;
import static dev.jlibra.poller.Conditions.transactionFound;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.MultiSignaturePublicKey;
import dev.jlibra.PublicKey;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ByteArrayArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableSignedTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransactionAuthenticatorMultiEd25519;
import dev.jlibra.admissioncontrol.transaction.LbrTypeTag;
import dev.jlibra.admissioncontrol.transaction.Signature;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.U64Argument;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.faucet.Faucet;
import dev.jlibra.client.views.Account;
import dev.jlibra.move.Move;
import dev.jlibra.poller.Wait;

public class MultisigTransactionTest {

    private static final Logger logger = LoggerFactory.getLogger(MultisigTransactionTest.class);

    @Before
    public void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testMultisigTransaction() {
        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        Faucet faucet = Faucet.builder()
                .build();

        // source account, multisig account with 32 keypairs, 30 threshold
        List<KeyPair> keyPairs = generateKeyPairs(32);
        MultiSignaturePublicKey multiPubKey = MultiSignaturePublicKey.create(
                keyPairs.stream().map(kp -> PublicKey.fromPublicKey(kp.getPublic())).collect(toList()),
                30);

        AuthenticationKey authenticationKey = AuthenticationKey.fromMultiSignaturePublicKey(multiPubKey);
        AccountAddress accountAddress = AccountAddress.fromAuthenticationKey(authenticationKey);
        faucet.mint(authenticationKey, 10 * 1_000_000);
        Wait.until(accountExists(accountAddress, client));

        // target account
        KeyPair targetAccount = generateKeyPairs(1).get(0);
        PublicKey publicKeyTarget = PublicKey.fromPublicKey(targetAccount.getPublic());
        AuthenticationKey authenticationKeyTarget = AuthenticationKey.fromPublicKey(publicKeyTarget);

        long transferAmount = 2_000_000;
        // Arguments for the peer to peer transaction
        U64Argument amountArgument = new U64Argument(transferAmount);
        AccountAddressArgument addressArgument = new AccountAddressArgument(
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        ByteArrayArgument authkeyPrefixArgument = new ByteArrayArgument(
                authenticationKeyTarget.toByteArray().subseq(0, 16));

        logger.info("Sender auth key {}, sender address {}", authenticationKey, accountAddress);
        logger.info("Receiver auth key {}, sender address {}", authenticationKeyTarget,
                AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        int sequenceNumber = 0;
        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(sequenceNumber)
                .maxGasAmount(2_000_000)
                .gasUnitPrice(1)
                .senderAccount(accountAddress)
                .expirationTime(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .code(Move.peerToPeerTransferAsBytes())
                        .typeArguments(Arrays.asList(new LbrTypeTag()))
                        .addArguments(addressArgument, authkeyPrefixArgument, amountArgument)
                        .build())
                .build();

        // add 30 signatures, leave out 1st and last signer
        Signature signature = Signature.newMultisignature();
        for (int i = 1; i < 31; i++) {
            signature = Signature.addSignatureToMultiSignature(signature, i, transaction, keyPairs.get(i).getPrivate());
        }

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
                .getAccountState(AccountAddress.fromAuthenticationKey(authenticationKeyTarget));

        assertThat(targetAccountState.balances().get(0).amount(), is(transferAmount));
    }

    private KeyPairGenerator getKeyPairGenerator() {
        try {
            return KeyPairGenerator.getInstance("Ed25519", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
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
