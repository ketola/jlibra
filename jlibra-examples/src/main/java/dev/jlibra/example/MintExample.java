package dev.jlibra.example;

import static dev.jlibra.poller.Conditions.accountHasPositiveBalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;
import dev.jlibra.faucet.Faucet;
import dev.jlibra.poller.Wait;

/**
 * Calls the faucet service http endpoint with parameters authentication key and
 * amount (in microLibras)
 * 
 * If the request is successful, the service returns an id number and the minted
 * amount should be available for the account.
 * 
 * This works only for the libra testnet, to mint in another (t. ex. local)
 * environment, you would need to create a mint transaction which requires
 * special permissions.
 * 
 */
public class MintExample {

    private static final String CURRENCY = "Coin1";
    private static final Logger logger = LoggerFactory.getLogger(MintExample.class);

    public static void main(String[] args) {
        AuthenticationKey authenticationKey = AuthenticationKey
                .fromHexString("c0c19d6b1d48371ea28f0cdc5f74bba7b3f7e8e38f8c8393f281a2f0792a2849");

        Faucet faucet = Faucet.builder().build();
        faucet.mint(authenticationKey, 100L * 1_000_000L, CURRENCY);

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();

        Wait.until(accountHasPositiveBalance(AccountAddress.fromAuthenticationKey(authenticationKey), client));

        Account account = client.getAccount(AccountAddress.fromAuthenticationKey(authenticationKey));
        logger.info("Balance: {} {}", account.balances().get(0).amount() / 1_000_000,
                account.balances().get(0).currency());
    }
}
