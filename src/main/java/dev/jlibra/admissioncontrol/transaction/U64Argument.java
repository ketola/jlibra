package dev.jlibra.admissioncontrol.transaction;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;

public class U64Argument implements TransactionArgument {

    private long value;

    public U64Argument(long value) {
        this.value = value;
    }

    @Override
    public byte[] toByteArray() {
        return ByteBuffer.allocate(Long.BYTES).order(LITTLE_ENDIAN).putLong(value)
                .order(LITTLE_ENDIAN).array();
    }

    @Override
    public Type type() {
        return Type.U64;
    }

}
