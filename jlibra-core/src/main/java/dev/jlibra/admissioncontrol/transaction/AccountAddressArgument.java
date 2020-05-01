package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.lcs.LCS;

public class AccountAddressArgument implements TransactionArgument {

    private AccountAddress value;

    public AccountAddressArgument(AccountAddress address) {
        this.value = address;
    }

    @LCS.Field(value = 0, fixedLength = true)
    public AccountAddress getValue() {
        return value;
    }

}
