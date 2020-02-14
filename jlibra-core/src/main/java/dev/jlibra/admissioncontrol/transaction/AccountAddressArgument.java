package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.lcs.LCS;

@LCS.ExternallyTaggedEnumeration(dev.jlibra.serialization.lcs.type.TransactionArgument.Address)
public class AccountAddressArgument implements TransactionArgument {

    public static final int PREFIX = 1;

    private AccountAddress value;

    public AccountAddressArgument(AccountAddress address) {
        this.value = address;
    }

    @LCS.Field(value = 0, fixedLength = true)
    public AccountAddress getValue() {
        return value;
    }

}
