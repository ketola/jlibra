package dev.jlibra.mnemonic;

import java.nio.ByteBuffer;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ChildNumber {

    public final long data;

    public ChildNumber(long data) {
        this.data = data;
    }

    public ChildNumber increment() {
        return new ChildNumber(data + 1);
    }

    public byte[] getData() {
        return ByteBuffer.allocate(Long.BYTES).putLong(data).array();
    }
}
