package dev.jlibra.example;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;

public class GetAccountStateExample {

    private static final Logger logger = LoggerFactory.getLogger(GetAccountStateExample.class);

    public static void main(String[] args) throws Exception {
        String address = "0cb6b167f553151f5ae7ab5364629dc4";

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        Account accountView = client.getAccountState(AccountAddress.fromHexString(address));

        logger.info("Authentication key: {}", accountView.authenticationKey());
        logger.info("Received events key: {}", accountView.receivedEventsKey());
        logger.info("Sent events key: {}", accountView.sentEventsKey());
        logger.info("Balance (micro): {}", accountView.balances().get(0).amount());
        logger.info("Balance: {}", new BigDecimal(accountView.balances().get(0).amount())
                .divide(BigDecimal.valueOf(1_000_000)));
        logger.info("Currency: {}", accountView.balances().get(0).currency());
        logger.info("Sequence number: {}", accountView.sequenceNumber());
        logger.info("Delegated withdrawal capability: {}", accountView.delegatedWithdrawalCapability());
        logger.info("Delegated key rotation capability: {}", accountView.delegatedKeyRotationCapability());
    }
}
