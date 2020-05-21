package dev.jlibra.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Transaction;

public class GetAccountTransactionBySequenceNumberExample {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountTransactionBySequenceNumberExample.class);

    public static void main(String[] args) {
        String address = "2ab3189806488e73014e2e429e45c143";
        int sequenceNumber = 0;

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        Transaction t = client.getAccountTransaction(AccountAddress.fromHexString(address), sequenceNumber, true);

        logger.info("Transaction: {}", t);
    }
}
