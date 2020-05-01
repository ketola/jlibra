package dev.jlibra.mnemonic;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteSequence;

public class SecretKey {

    private final ByteSequence byteSequence;

    public SecretKey(ByteSequence byteSequence) {
        this.byteSequence = byteSequence;
        byte[] data = byteSequence.toArray();
        if (data == null || data.length != 32) {
            throw new LibraRuntimeException(
                    "SecretKey requires 32 bytes but found " + (data == null ? 0 : data.length));
        }
    }

    public ByteSequence getByteSequence() {
        return byteSequence;
    }

    @Override
    public String toString() {
        return byteSequence.toString();
    }
}
