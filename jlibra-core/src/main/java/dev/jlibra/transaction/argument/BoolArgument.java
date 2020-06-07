package dev.jlibra.transaction.argument;

import dev.jlibra.serialization.lcs.LCS;

public class BoolArgument implements TransactionArgument {

    private boolean value;

    public BoolArgument(boolean value) {
        this.value = value;
    }

    @LCS.Field(0)
    public byte getValue() {
        return (byte) (value ? 1 : 0);
    }

}
