package dev.jlibra.poller;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.DiemClient;
import dev.jlibra.client.views.Account;

public class Conditions {
    public static WaitCondition accountExists(AccountAddress accountAddress, DiemClient libraClient) {
        return () -> libraClient.getAccount(accountAddress) != null;
    }

    public static WaitCondition accountHasPositiveBalance(AccountAddress accountAddress, DiemClient libraClient) {
        return () -> {
            Account account = libraClient.getAccount(accountAddress);
            return account != null && account.balances().stream().anyMatch(b -> b.amount() > 0);
        };
    }

    public static WaitCondition transactionFound(AccountAddress accountAddress, long seqNumber,
            DiemClient libraClient) {
        return () -> libraClient.getAccountTransaction(accountAddress, seqNumber, false) != null;
    }
}
