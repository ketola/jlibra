package dev.jlibra;

import java.security.PublicKey;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.LibraSerializable;

public class AccountAddress {

    private final ByteSequence byteSequence;

    private AccountAddress(ByteSequence byteSequence) {
        this.byteSequence = byteSequence;
    }

    public static AccountAddress ofPublicKey(PublicKey publicKey) {
        return new AccountAddress(KeyUtils.toByteSequenceLibraAddress(ByteSequence.from(publicKey.getEncoded())));
    }

    public static AccountAddress ofByteSequence(ByteSequence address) {
        return new AccountAddress(address);
    }

    public ByteSequence getByteSequence() {
        return byteSequence;
    }

    @Override
    public String toString() {
        return String.format("AccountAddress: %s", byteSequence);
    }

}
