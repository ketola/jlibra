package dev.jlibra.poller;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Account;

public class Conditions {
    public static WaitCondition accountExists(AccountAddress accountAddress, LibraClient libraClient) {
        return () -> libraClient.getAccount(accountAddress) != null;
    }

    public static WaitCondition accountHasPositiveBalance(AccountAddress accountAddress, LibraClient libraClient) {
        return () -> {
            Account account = libraClient.getAccount(accountAddress);
            return account != null && account.balances().stream().anyMatch(b -> b.amount() > 0);
        };
    }

    public static WaitCondition transactionFound(AccountAddress accountAddress, long seqNumber,
            LibraClient libraClient) {
        return () -> libraClient.getAccountTransaction(accountAddress, seqNumber, false) != null;
    }
}
