package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;
import dev.jlibra.serialization.lcs.LCS;

@LCS.Enum(ordinal = 1)
public class AccountAddressArgument implements TransactionArgument {

    public static final int PREFIX = 1;

    private AccountAddress value;

    public AccountAddressArgument(AccountAddress address) {
        this.value = address;
    }

    @LCS.Field(ordinal = 0)
    public AccountAddress getValue() {
        return value;
    }

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendWithoutLengthInformation(value.getByteSequence())
                .toByteSequence();
    }
}
