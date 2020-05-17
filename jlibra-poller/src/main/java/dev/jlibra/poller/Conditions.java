package dev.jlibra.poller;

import dev.jlibra.AccountAddress;
import dev.jlibra.client.LibraClient;

public class Conditions {
    public static WaitCondition accountExists(AccountAddress accountAddress, LibraClient libraClient) {
        return () -> libraClient.getAccountState(accountAddress) != null;
    }

    public static WaitCondition transactionFound(AccountAddress accountAddress, long seqNumber,
            LibraClient libraClient) {
        return () -> libraClient.getAccountTransaction(accountAddress, seqNumber, false) != null;
    }
}
