package dev.jlibra.example;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.transaction.Transaction;

public class GetAccountTransactionsExample {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountTransactionsExample.class);

    public static void main(String[] args) {
        String address = "330952f689e737312bbfd6c9f85ad31e";

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();

        List<Transaction> transactions = client.getAccountTransactions(AccountAddress.fromHexString(address), 0, 5,
                true);

        logger.info("Transactions: {}", transactions);
    }
}
