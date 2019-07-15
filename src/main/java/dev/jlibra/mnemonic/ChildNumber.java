package dev.jlibra.mnemonic;

import javax.annotation.concurrent.Immutable;

import static dev.jlibra.mnemonic.ByteUtils.longToBytes;

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
        return longToBytes(data);
    }
}
