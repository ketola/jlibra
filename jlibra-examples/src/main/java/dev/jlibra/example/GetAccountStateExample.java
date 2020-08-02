package dev.jlibra.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;

public class GetAccountStateExample {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountStateExample.class);

    public static void main(String[] args) throws Exception {
        String address = "fc4c85260cdccd2ee25f217da715e5dc";

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();

        Account accountView = client.getAccount(AccountAddress.fromHexString(address));

        logger.info("Account: {}", accountView);
    }
}
