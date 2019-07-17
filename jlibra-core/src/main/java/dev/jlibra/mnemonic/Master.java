package dev.jlibra.mnemonic;

import javax.annotation.concurrent.Immutable;

/**
 * Master is a set of raw bytes that are used for child key derivation
  */
@Immutable
public class Master {

    private byte[] data;

    public Master(byte[] data) {
        if (data == null || data.length != 32) {
            throw new RuntimeException("Master requires 32 bytes but found " + (data == null ? 0 : data.length));
        }

        this.data = data.clone();
    }

    public byte[] getData() {
        return data.clone();
    }
}
