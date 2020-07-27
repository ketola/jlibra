package dev.jlibra.example;

import static dev.jlibra.poller.Conditions.accountHasPositiveBalance;
import static dev.jlibra.poller.Conditions.transactionFound;
import static java.util.Arrays.asList;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.PublicKey;
import dev.jlibra.client.LibraClient;
import dev.jlibra.faucet.Faucet;
import dev.jlibra.move.Move;
import dev.jlibra.poller.Wait;
import dev.jlibra.transaction.ChainId;
import dev.jlibra.transaction.ImmutableScript;
import dev.jlibra.transaction.ImmutableSignedTransaction;
import dev.jlibra.transaction.ImmutableTransaction;
import dev.jlibra.transaction.ImmutableTransactionAuthenticatorEd25519;
import dev.jlibra.transaction.Signature;
import dev.jlibra.transaction.SignedTransaction;
import dev.jlibra.transaction.Struct;
import dev.jlibra.transaction.Transaction;
import dev.jlibra.transaction.argument.AccountAddressArgument;
import dev.jlibra.transaction.argument.BoolArgument;
import dev.jlibra.transaction.argument.U64Argument;
import dev.jlibra.transaction.argument.U8VectorArgument;

public class CreateChildVaspAccountExample {

    private static final Logger logger = LoggerFactory.getLogger(CreateChildVaspAccountExample.class);

    /**
     * Based on Libra white paper, VASPs are: "Virtual Asset Service Providers
     * (exchanges and custodial wallets) that are registered or licensed as VASPs in
     * a Financial Action Task Force (FATF) member jurisdiction, or are registered
     * or licensed in a FATF member jurisdiction and are permitted to perform VASP
     * activities under such license or registration (Regulated VASPs)"
     * 
     */
    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();
        Faucet faucet = Faucet.builder().build();

        KeyPair parentVaspKeyPair = generateKeyPair();

        // In the testnet new VASP accounts can be created by minting money to a new
        // address:
        AuthenticationKey parentVaspAuthKey = AuthenticationKey.fromPublicKey(parentVaspKeyPair.getPublic());
        faucet.mint(parentVaspAuthKey, 100L * 1_000_000L, "LBR");
        Wait.until(accountHasPositiveBalance(AccountAddress.fromAuthenticationKey(parentVaspAuthKey), client));

        int parentVaspSequenceNumber = 0;
        logger.info("Parent vasp authentication key: {} address: {}", parentVaspAuthKey,
                AccountAddress.fromAuthenticationKey(parentVaspAuthKey));

        // A child account for the VASP account is created by the parent account using
        // the
        // "create_child_vasp_account" move script
        //
        // The parameters are the new account address, auth key prefix and a boolean
        // telling if all the supported currencies should be added to the new account
        KeyPair childVaspAccountKeyPair = generateKeyPair();
        AuthenticationKey childVaspAccountAuthKey = AuthenticationKey
                .fromPublicKey(childVaspAccountKeyPair.getPublic());
        AccountAddress childVaspAccountAddress = AccountAddress.fromAuthenticationKey(childVaspAccountAuthKey);

        logger.info("Child vasp authentication key: {} address: {}", childVaspAccountAuthKey, childVaspAccountAddress);

        AccountAddressArgument childAccountArgument = new AccountAddressArgument(childVaspAccountAddress);
        U8VectorArgument authKeyPrefixArgument = new U8VectorArgument(childVaspAccountAuthKey.prefix());
        BoolArgument createAllCurrenciesArgument = new BoolArgument(false);
        U64Argument initialBalanceArgument = new U64Argument(1_000_000);

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(parentVaspSequenceNumber)
                .maxGasAmount(640000)
                .gasUnitPrice(1)
                .gasCurrencyCode("LBR")
                .sender(AccountAddress.fromAuthenticationKey(parentVaspAuthKey))
                .expirationTimestampSecs(Instant.now().getEpochSecond() + 60)
                .payload(ImmutableScript.builder()
                        .typeArguments(asList(Struct.typeTagForCurrency("LBR")))
                        .code(Move.createChildVaspAccount())
                        .addArguments(childAccountArgument, authKeyPrefixArgument, createAllCurrenciesArgument,
                                initialBalanceArgument)
                        .build())
                .chainId(ChainId.TESTNET)
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticatorEd25519.builder()
                        .publicKey(PublicKey.fromPublicKey(parentVaspKeyPair.getPublic()))
                        .signature(Signature.signTransaction(transaction, parentVaspKeyPair.getPrivate()))
                        .build())
                .transaction(transaction)
                .build();

        client.submit(signedTransaction);

        Wait.until(transactionFound(AccountAddress.fromAuthenticationKey(parentVaspAuthKey),
                parentVaspSequenceNumber, client));

        logger.info("----------------------------");
        logger.info("Parent VASP account: {}",
                client.getAccount(AccountAddress.fromAuthenticationKey(parentVaspAuthKey)));
        logger.info("----------------------------");

        logger.info("----------------------------");
        logger.info("Child VASP account: {}", client.getAccount(childVaspAccountAddress));
        logger.info("----------------------------");
    }

    private static KeyPair generateKeyPair() {
        KeyPairGenerator kpGen = getKeyPairGenerator();
        return kpGen.generateKeyPair();
    }

    private static KeyPairGenerator getKeyPairGenerator() {
        try {
            return KeyPairGenerator.getInstance("Ed25519", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

}
