package dev.jlibra;

import java.security.PublicKey;

import dev.jlibra.admissioncontrol.transaction.FixedLengthByteSequence;
import dev.jlibra.admissioncontrol.transaction.ImmutableFixedLengthByteSequence;
import dev.jlibra.serialization.ByteSequence;

public class AccountAddress {

    public static FixedLengthByteSequence ofPublicKey(PublicKey publicKey) {
        return ImmutableFixedLengthByteSequence.builder()
                .value(KeyUtils.toByteSequenceLibraAddress(ByteSequence.from(publicKey.getEncoded())))
                .build();
    }

    public static FixedLengthByteSequence ofByteSequence(ByteSequence address) {
        return ImmutableFixedLengthByteSequence.builder()
                .value(address)
                .build();
    }

}
