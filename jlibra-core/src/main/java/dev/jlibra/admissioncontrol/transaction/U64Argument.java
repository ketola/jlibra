package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.lcs.LCS;

@LCS.ExternallyTaggedEnumeration(dev.jlibra.serialization.lcs.type.TransactionArgument.U64)
public class U64Argument implements TransactionArgument {

    private long value;

    public static final int PREFIX = 0;

    public U64Argument(long value) {
        this.value = value;
    }

    @LCS.Field(0)
    public long getValue() {
        return value;
    }

}
