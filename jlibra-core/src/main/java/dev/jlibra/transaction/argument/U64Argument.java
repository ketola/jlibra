package dev.jlibra.transaction.argument;

import dev.jlibra.serialization.lcs.LCS;

public class U64Argument implements TransactionArgument {

    private long value;

    public U64Argument(long value) {
        this.value = value;
    }

    @LCS.Field(0)
    public long getValue() {
        return value;
    }

}
