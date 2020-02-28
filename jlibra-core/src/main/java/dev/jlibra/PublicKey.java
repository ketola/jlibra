package dev.jlibra;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class PublicKey implements ByteSequence {

    private ByteArray bytes;

    private PublicKey(ByteArray bytes) {
        this.bytes = bytes;
    }

    public static PublicKey ofPublicKey(java.security.PublicKey pk) {
        return new PublicKey(KeyUtils.stripPublicKeyPrefix(ByteArray.from(pk.getEncoded())));
    }

    public static PublicKey ofByteSequence(ByteArray byteSequence) {
        return new PublicKey(byteSequence);
    }

    @Override
    public byte[] toArray() {
        return bytes.toArray();
    }

    @Override
    public ByteString toByteString() {
        return bytes.toByteString();
    }

}
