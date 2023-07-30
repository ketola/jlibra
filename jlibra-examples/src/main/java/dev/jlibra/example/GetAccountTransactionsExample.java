package dev.jlibra.example;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.DiemClient;
import dev.jlibra.client.views.transaction.Transaction;

public class GetAccountTransactionsExample {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountTransactionsExample.class);

    public static void main(String[] args) {
        String address = "9ed85589fb5617a18521636181a12b88";

        DiemClient client = DiemClient.builder()
                .withUrl("http://localhost:8080")
                .build();

        List<Transaction> transactions = client.getAccountTransactions(AccountAddress.fromHexString(address), 0, 5,
                true);

        logger.info("Transactions: {}", transactions);
    }
}
