package dev.jlibra;

import java.security.PublicKey;

import dev.jlibra.serialization.ByteSequence;

public class AccountAddress extends ByteSequence {

    private AccountAddress(ByteSequence bytes) {
        super(bytes.toArray());
    }

    public static AccountAddress ofPublicKey(PublicKey publicKey) {
        return new AccountAddress(KeyUtils.toByteSequenceLibraAddress(ByteSequence.from(publicKey.getEncoded())));
    }

    public static AccountAddress ofByteSequence(ByteSequence byteSequence) {
        return new AccountAddress(byteSequence);
    }

    public static AccountAddress ofHexString(String hexString) {
        return new AccountAddress(ByteSequence.from(hexString));
    }

}
