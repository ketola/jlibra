package dev.jlibra.transaction.argument;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.bcs.BCS;

@Value.Immutable
public interface U8VectorArgument extends TransactionArgument {

    @BCS.Field(0)
    ByteSequence value();

    public static U8VectorArgument from(ByteSequence value) {
        return ImmutableU8VectorArgument.builder()
                .value(value)
                .build();
    }

}
