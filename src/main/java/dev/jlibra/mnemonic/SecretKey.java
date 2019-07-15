package dev.jlibra.mnemonic;

import org.bouncycastle.util.encoders.Hex;

import javax.annotation.concurrent.Immutable;

@Immutable
public class SecretKey {

    private final byte[] data;

    public SecretKey(byte[] data) {
        if (data == null || data.length != 32) {
            throw new RuntimeException("SecretKey requires 32 bytes but found " + (data == null ? 0 : data.length));
        }

        this.data = data.clone();
    }

    public byte[] getData() {
        return data.clone();
    }

    @Override
    public String toString() {
        return Hex.toHexString(data);
    }
}
