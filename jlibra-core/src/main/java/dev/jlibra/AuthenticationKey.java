package dev.jlibra;

import java.security.PublicKey;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class AuthenticationKey implements ByteSequence {

    private ByteArray bytes;

    private AuthenticationKey(ByteArray bytes) {
        this.bytes = bytes;
    }

    public static AuthenticationKey fromPublicKey(PublicKey publicKey) {
        return new AuthenticationKey(
                Hash.ofInput(KeyUtils.stripPublicKeyPrefix(ByteArray.from(publicKey.getEncoded()))).hash());
    }

    public static AuthenticationKey fromByteArray(ByteArray bytes) {
        return new AuthenticationKey(bytes);
    }

    public static AuthenticationKey fromHexString(String hexString) {
        return new AuthenticationKey(ByteArray.from(hexString));
    }

    public ByteArray toByteArray() {
        return ByteArray.from(bytes.toArray());
    }

    @Override
    public byte[] toArray() {
        return bytes.toArray();
    }

    @Override
    public ByteString toByteString() {
        return bytes.toByteString();
    }

    @Override
    public String toString() {
        return bytes.toString();
    }

}
