package dev.jlibra.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.transaction.Transaction;

public class GetAccountTransactionBySequenceNumberExample {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountTransactionBySequenceNumberExample.class);

    public static void main(String[] args) {
        String address = "330952f689e737312bbfd6c9f85ad31e";
        int sequenceNumber = 0;

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();

        Transaction t = client.getAccountTransaction(AccountAddress.fromHexString(address), sequenceNumber, true);

        logger.info("Transaction: {}", t);
    }
}
