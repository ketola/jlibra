package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
@LCS.Structure(builderClass = ImmutableAccountAddressArgument.class)
public interface AccountAddressArgument extends TransactionArgument {

    public static final int PREFIX = 1;

    @LCS.Field(value = 0, fixedLength = true)
    AccountAddress value();

}
