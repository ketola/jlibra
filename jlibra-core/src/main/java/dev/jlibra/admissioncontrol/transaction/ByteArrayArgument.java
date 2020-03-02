package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;

@LCS.ExternallyTaggedEnumeration(dev.jlibra.serialization.lcs.type.TransactionArgument.ByteArray)
public class ByteArrayArgument implements TransactionArgument {

    public static final int PREFIX = 2;

    private ByteSequence bytes;

    public ByteArrayArgument(ByteSequence bytes) {
        this.bytes = bytes;
    }

    @LCS.Field(0)
    public ByteSequence getValue() {
        return bytes;
    }

}
