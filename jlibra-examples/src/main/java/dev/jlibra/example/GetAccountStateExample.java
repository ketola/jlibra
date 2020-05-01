package dev.jlibra.example;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;

public class GetAccountStateExample {

    private static final Logger logger = LogManager.getLogger(GetAccountStateExample.class);

    public static void main(String[] args) throws Exception {
        String address = "155ba875cf0a037e89b86230da4280f1";

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        Account accountView = client.getAccountState(address);

        logger.info("Authentication key: {}", accountView.authenticationKey());
        logger.info("Received events key: {}", accountView.receivedEventsKey());
        logger.info("Sent events key: {}", accountView.sentEventsKey());
        logger.info("Balance (micro): {}", accountView.balance().amount());
        logger.info("Balance: {}", new BigDecimal(accountView.balance().amount())
                .divide(BigDecimal.valueOf(1_000_000)));
        logger.info("Currency: {}", accountView.balance().currency());
        logger.info("Sequence number: {}", accountView.sequenceNumber());
        logger.info("Delegated withdrawal capability: {}", accountView.delegatedWithdrawalCapability());
        logger.info("Delegated key rotation capability: {}", accountView.delegatedKeyRotationCapability());
    }
}
