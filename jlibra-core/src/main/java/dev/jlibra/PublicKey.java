package dev.jlibra;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class PublicKey implements ByteSequence {

    public static int PUBLIC_KEY_LENGTH = 32;

    private ByteArray bytes;

    private PublicKey(ByteArray bytes) {
        this.bytes = bytes;
    }

    public static PublicKey fromPublicKey(java.security.PublicKey pk) {
        return new PublicKey(KeyUtils.stripPublicKeyPrefix(ByteArray.from(pk.getEncoded())));
    }

    public static PublicKey fromByteSequence(ByteArray byteSequence) {
        return new PublicKey(byteSequence);
    }

    @Override
    public byte[] toArray() {
        return bytes.toArray();
    }
}
