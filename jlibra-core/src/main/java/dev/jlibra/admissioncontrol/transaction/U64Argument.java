package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;
import dev.jlibra.serialization.lcs.LCS;

@LCS.Enum(ordinal = 0)
public class U64Argument implements TransactionArgument {

    private long value;

    public static final int PREFIX = 0;

    public U64Argument(long value) {
        this.value = value;
    }

    @LCS.Field(ordinal = 0)
    public long getValue() {
        return value;
    }

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendLong(value)
                .toByteSequence();
    }

}
