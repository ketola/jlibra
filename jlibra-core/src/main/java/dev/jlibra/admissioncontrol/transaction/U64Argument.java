package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;

public class U64Argument implements TransactionArgument {

    private long value;

    private static final int PREFIX = 0;

    public U64Argument(long value) {
        this.value = value;
    }

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendLong(value)
                .toByteSequence();
    }

}
