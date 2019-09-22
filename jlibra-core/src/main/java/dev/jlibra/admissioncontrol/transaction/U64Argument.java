package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.Serializer;

public class U64Argument implements TransactionArgument {

    private long value;

    private static final int PREFIX = 0;

    public U64Argument(long value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendLong(value)
                .toByteArray();
    }

    @Override
    public Type type() {
        return Type.U64;
    }

}
