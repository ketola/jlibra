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
        String address = "25407727b0b497be4f31f8dd82843a9c";

        DiemClient client = DiemClient.builder()
                .withUrl("https://testnet.diem.com/v1")
                .build();

        List<Transaction> transactions = client.getAccountTransactions(AccountAddress.fromHexString(address), 0, 5,
                true);

        logger.info("Transactions: {}", transactions);
    }
}
