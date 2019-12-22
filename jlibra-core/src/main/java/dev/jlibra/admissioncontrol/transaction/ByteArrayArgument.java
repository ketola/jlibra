package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;

public class ByteArrayArgument implements TransactionArgument {

    private static final int PREFIX = 3;

    private ByteSequence bytes;

    public ByteArrayArgument(ByteSequence bytes) {
        this.bytes = bytes;
    }

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .append(bytes)
                .toByteSequence();
    }
}
