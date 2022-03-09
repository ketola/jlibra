package dev.jlibra.example;

import static dev.jlibra.poller.Conditions.accountHasPositiveBalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.client.DiemClient;
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
 * This works only for the diem testnet, to mint in another (t. ex. local)
 * environment, you would need to create a mint transaction which requires
 * special permissions.
 * 
 */
public class MintExample {

    private static final String CURRENCY = "XUS";
    private static final Logger logger = LoggerFactory.getLogger(MintExample.class);

    public static void main(String[] args) {
        AuthenticationKey authenticationKey = AuthenticationKey
                .fromHexString("8f30171675008e598af4d57c159dad7240fc2800a33e1790730244ab931630a2");

        Faucet faucet = Faucet.builder().build();
        faucet.mint(authenticationKey, 100L * 1_000_000L, CURRENCY);

        DiemClient client = DiemClient.builder()
                .withUrl("http://testnet.diem.com/v1")
                .build();

        Wait.until(accountHasPositiveBalance(AccountAddress.fromAuthenticationKey(authenticationKey), client));

        Account account = client.getAccount(AccountAddress.fromAuthenticationKey(authenticationKey));
        logger.info("Balance: {} {}", account.balances().get(0).amount() / 1_000_000,
                account.balances().get(0).currency());
    }
}
