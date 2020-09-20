package dev.jlibra.transaction.argument;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
public interface AccountAddressArgument extends TransactionArgument {

    @LCS.Field(value = 0, fixedLength = true)
    AccountAddress value();

    public static AccountAddressArgument from(AccountAddress accountAddress) {
        return ImmutableAccountAddressArgument.builder()
                .value(accountAddress)
                .build();
    }

}
