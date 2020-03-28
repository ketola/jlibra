package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;

public class ByteArrayArgument implements TransactionArgument {

    private ByteSequence bytes;

    public ByteArrayArgument(ByteSequence bytes) {
        this.bytes = bytes;
    }

    @LCS.Field(0)
    public ByteSequence getValue() {
        return bytes;
    }

}
