package dev.jlibra;

import java.security.PublicKey;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class AccountAddress implements ByteSequence {

    private ByteArray bytes;

    private AccountAddress(ByteArray bytes) {
        this.bytes = bytes;
    }

    public static AccountAddress fromPublicKey(PublicKey publicKey) {
        return new AccountAddress(
                Hash.ofInput(KeyUtils.stripPublicKeyPrefix(ByteArray.from(publicKey.getEncoded()))).hash());
    }

    public static AccountAddress fromByteArray(ByteArray bytes) {
        return new AccountAddress(bytes);
    }

    public static AccountAddress fromHexString(String hexString) {
        return new AccountAddress(ByteArray.from(hexString));
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
