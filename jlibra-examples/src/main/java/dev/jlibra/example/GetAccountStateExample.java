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
        String address = "8a0461f91654bb308638907907e348cc";

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        Account accountView = client.getAccountState(AccountAddress.fromHexString(address));

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
