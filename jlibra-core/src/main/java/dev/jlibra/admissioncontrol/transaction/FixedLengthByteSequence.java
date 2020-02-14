package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
public interface FixedLengthByteSequence {

    @LCS.Field(0)
    ByteSequence getValue();

    public static FixedLengthByteSequence ofByteSequence(ByteSequence b) {
        return ImmutableFixedLengthByteSequence.builder()
                .value(b)
                .build();
    }
}
