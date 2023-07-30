package dev.jlibra.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.DiemClient;
import dev.jlibra.client.views.Account;

public class GetAccountStateExample {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountStateExample.class);

    public static void main(String[] args) throws Exception {
        String address = "79153273a34e0aadf26c963367973760";

        DiemClient client = DiemClient.builder()
                .withUrl("http://localhost:8080")
                .build();

        Account accountView = client.getAccount(AccountAddress.fromHexString(address));

        logger.info("Account: {}", accountView);
    }
}
