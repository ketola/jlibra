package dev.jlibra.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;

public class GetAccountStateExample {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountStateExample.class);

    public static void main(String[] args) throws Exception {
        String address = "d895681232b817442754b1dc3f80ecf7";

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        Account accountView = client.getAccountState(AccountAddress.fromHexString(address));

        logger.info("Account: {}", accountView);
    }
}
