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

    private static final Logger logger = LoggerFactory.getLogger(MintExample.class);

    public static void main(String[] args) {
        AuthenticationKey authenticationKey = AuthenticationKey
                .fromHexString("efb54d5bd7e280c637eb7772d211be172ab3189806488e73014e2e429e45c143");

        Faucet faucet = Faucet.builder().build();
        faucet.mint(authenticationKey, 100L * 1_000_000L, "LBR");

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();

        Wait.until(accountHasPositiveBalance(AccountAddress.fromAuthenticationKey(authenticationKey), client));

        Account account = client.getAccount(AccountAddress.fromAuthenticationKey(authenticationKey));
        logger.info("Balance: {} {}", account.balances().get(0).amount() / 1_000_000,
                account.balances().get(0).currency());
    }
}
