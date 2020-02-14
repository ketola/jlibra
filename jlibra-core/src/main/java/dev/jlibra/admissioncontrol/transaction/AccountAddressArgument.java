package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;

@LCS.ExternallyTaggedEnumeration(dev.jlibra.serialization.lcs.type.TransactionArgument.Address)
public class AccountAddressArgument implements TransactionArgument {

    public static final int PREFIX = 1;

    private ByteSequence value;

    public AccountAddressArgument(ByteSequence address) {
        this.value = address;
    }

    @LCS.Field(0)
    public ByteSequence getValue() {
        return value;
    }

}
