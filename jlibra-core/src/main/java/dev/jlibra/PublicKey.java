package dev.jlibra;

import dev.jlibra.serialization.ByteSequence;

public class PublicKey extends ByteSequence {

    private PublicKey(ByteSequence bytes) {
        super(bytes.toArray());
    }

    public static PublicKey ofPublicKey(java.security.PublicKey pk) {
        return new PublicKey(KeyUtils.stripPublicKeyPrefix(ByteSequence.from(pk.getEncoded())));
    }

    public static PublicKey ofByteSequence(ByteSequence byteSequence) {
        return new PublicKey(byteSequence);
    }

}
