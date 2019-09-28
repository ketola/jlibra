package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.Serializer;

public class ByteArrayArgument implements TransactionArgument {

    private static final int PREFIX = 3;

    private byte[] bytes;

    public ByteArrayArgument(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public byte[] serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendByteArray(bytes)
                .toByteArray();
    }
}
