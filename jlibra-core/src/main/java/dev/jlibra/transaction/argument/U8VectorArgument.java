package dev.jlibra.transaction.argument;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;

public class U8VectorArgument implements TransactionArgument {

    private ByteSequence bytes;

    public U8VectorArgument(ByteSequence bytes) {
        this.bytes = bytes;
    }

    @LCS.Field(0)
    public ByteSequence getValue() {
        return bytes;
    }

}
