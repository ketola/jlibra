package dev.jlibra.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.DiemClient;
import dev.jlibra.client.views.transaction.Transaction;

public class GetAccountTransactionBySequenceNumberExample {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountTransactionBySequenceNumberExample.class);

    public static void main(String[] args) {
        String address = "c1e431460cae9772400fcd254e3cf912";
        int sequenceNumber = 0;

        DiemClient client = DiemClient.builder()
                .withUrl("https://testnet.diem.com/v1")
                .build();

        Transaction t = client.getAccountTransaction(AccountAddress.fromHexString(address), sequenceNumber, true);

        logger.info("Transaction: {}", t);
    }
}
