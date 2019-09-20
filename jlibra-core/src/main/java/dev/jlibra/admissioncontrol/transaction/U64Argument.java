package dev.jlibra.admissioncontrol.transaction;

import static dev.jlibra.serialization.CanonicalSerialization.join;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;

import org.bouncycastle.util.encoders.Hex;

public class U64Argument implements TransactionArgument {

    private long value;

    private static final byte[] PREFIX = Hex.decode("00000000");

    public U64Argument(long value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        return join(PREFIX, ByteBuffer.allocate(Long.BYTES).order(LITTLE_ENDIAN).putLong(value)
                .order(LITTLE_ENDIAN).array());
    }

    @Override
    public Type type() {
        return Type.U64;
    }

}
