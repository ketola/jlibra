package dev.jlibra.example;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.transaction.Transaction;

/**
 * The GetTransactions query allows you to query transaction based on the
 * version number (see:
 * https://developers.libra.org/docs/reference/glossary#version)
 * 
 */
public class GetTransactionsExample {

    private static final Logger logger = LoggerFactory.getLogger(GetTransactionsExample.class);

    public static void main(String[] args) {
        long start = 781507;
        long limit = 100;
        boolean fetchEvent = true;

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();

        List<Transaction> transactions = client.getTransactions(start, limit, fetchEvent);

        transactions.forEach(t -> {
            logger.info("Transaction: {}", t);
        });
    }
}
