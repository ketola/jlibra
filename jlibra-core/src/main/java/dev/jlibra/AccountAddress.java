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
        ByteArray encoded = ByteArray.from(publicKey.getEncoded());
        ByteArray pubkey = KeyUtils.stripPublicKeyPrefix(encoded);
        ByteArray hash = Hash.ofInput(pubkey).hash();
        ByteArray subseq = hash.subseq(16,
                16);
        return new AccountAddress(
                subseq);
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
