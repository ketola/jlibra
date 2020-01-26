package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;
import dev.jlibra.serialization.lcs.LCS;

@LCS.Enum(ordinal = 2)
public class ByteArrayArgument implements TransactionArgument {

    public static final int PREFIX = 2;

    private ByteSequence bytes;

    public ByteArrayArgument(ByteSequence bytes) {
        this.bytes = bytes;
    }

    @LCS.Field(ordinal = 0)
    public ByteSequence getValue() {
        return bytes;
    }

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .append(bytes)
                .toByteSequence();
    }
}
