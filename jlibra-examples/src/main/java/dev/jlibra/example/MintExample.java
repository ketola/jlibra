package dev.jlibra.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;
import dev.jlibra.faucet.Faucet;

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
                .fromHexString("83114168d1c4912ddfac857421cdcfa54fa2be7ad55936c5702e8b7e3fdedb05");

        Faucet faucet = Faucet.builder().build();
        faucet.mint(authenticationKey, 10L * 1_000_000L);

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        Account account = client.getAccountState(AccountAddress.fromAuthenticationKey(authenticationKey));
        logger.info("Balance: {} {}", account.balances().get(0).amount() / 1_000_000,
                account.balances().get(0).currency());
    }
}
