package dev.jlibra.example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.DiemRuntimeException;
import dev.jlibra.client.DiemAsyncClient;
import dev.jlibra.client.DiemClient;
import dev.jlibra.client.views.Account;
import dev.jlibra.client.views.CurrencyInfo;
import dev.jlibra.faucet.Faucet;
import dev.jlibra.poller.Conditions;
import dev.jlibra.poller.Wait;

/**
 * 
 * The DiemAsyncClient allows creating requests asynchronously. The example will
 * first create couple of test accounts, after that you should see all requests
 * being sent around same time and the example completes when a response has
 * been received to all of them.
 * 
 */
public class AsyncExample {

    private static final String CURRENCY = "XUS";

    private static final Logger logger = LoggerFactory.getLogger(AsyncExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        DiemAsyncClient client = DiemAsyncClient.builder()
                .withUrl("https://testnet.diem.com/v1")
                .build();

        // Note: minting and account creations is not done asynchronously, we just need
        // to set up the accounts
        // here for the example
        KeyPair kp1 = generateKeyPair();
        AuthenticationKey account1AuthKey = AuthenticationKey.fromPublicKey(kp1.getPublic());
        createAccount(account1AuthKey);

        KeyPair kp2 = generateKeyPair();
        AuthenticationKey account2AuthKey = AuthenticationKey.fromPublicKey(kp2.getPublic());
        createAccount(account2AuthKey);

        AccountAddress address1 = AccountAddress.fromAuthenticationKey(account1AuthKey);
        AccountAddress address2 = AccountAddress.fromAuthenticationKey(account2AuthKey);

        // Create async requests
        logger.info("----------------------------------------------------------------------");
        logger.info("-------------------- ASYNC REQUESTS START ----------------------------");
        logger.info("----------------------------------------------------------------------");

        CompletableFuture<Optional<Account>> account1 = client
                .getAccount(address1);

        CompletableFuture<Optional<Account>> account2 = client
                .getAccount(address2);

        CompletableFuture<Optional<List<CurrencyInfo>>> currencyInfo = client
                .currenciesInfo();

        // wait for all 3 requests to complete
        CompletableFuture.allOf(account1, account2, currencyInfo).get();

        logger.info(
                "\nAccount 1: {}\nAccount 2: {}\nCurrency info: {}\n",
                account1.get().get(), account2.get().get(), currencyInfo.get().get());
    }

    private static void createAccount(AuthenticationKey authKey) {
        DiemClient client = DiemClient.builder()
                .withUrl("https://testnet.diem.com/v1")
                .build();
        Faucet faucet = Faucet.builder().build();
        faucet.mint(authKey, 100L * 1_000_000L, CURRENCY);
        Wait.until(Conditions.accountExists(AccountAddress.fromAuthenticationKey(authKey), client));
    }

    private static KeyPair generateKeyPair() {
        KeyPairGenerator kpGen = getKeyPairGenerator();
        return kpGen.generateKeyPair();
    }

    private static KeyPairGenerator getKeyPairGenerator() {
        try {
            return KeyPairGenerator.getInstance("Ed25519", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new DiemRuntimeException("generate key pair failed", e);
        }
    }
}
