package dev.jlibra;

import dev.jlibra.admissioncontrol.transaction.ImmutableVariableLengthByteSequence;
import dev.jlibra.admissioncontrol.transaction.VariableLengthByteSequence;
import dev.jlibra.serialization.ByteSequence;

public class PublicKey {
    public static VariableLengthByteSequence ofPublicKey(java.security.PublicKey pk) {
        return ImmutableVariableLengthByteSequence.builder()
                .value(KeyUtils.stripPublicKeyPrefix(ByteSequence.from(pk.getEncoded())))
                .build();
    }
}
