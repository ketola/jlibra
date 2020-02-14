package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
public interface VariableLengthByteSequence {

    @LCS.Field(0)
    ByteSequence getValue();

    default ByteString toByteString() {
        return ByteString.copyFrom(getValue().toArray());
    }

    public static VariableLengthByteSequence ofByteSequence(ByteSequence b) {
        return ImmutableVariableLengthByteSequence.builder()
                .value(b)
                .build();
    }
}
