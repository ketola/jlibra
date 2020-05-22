package dev.jlibra.transaction.argument;

import dev.jlibra.serialization.lcs.LCS;

public class U8Argument implements TransactionArgument {

    private byte value;

    public U8Argument(byte value) {
        this.value = value;
    }

    @LCS.Field(0)
    public byte getValue() {
        return value;
    }

}
