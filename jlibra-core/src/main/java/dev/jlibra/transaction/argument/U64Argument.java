package dev.jlibra.transaction.argument;

import org.immutables.value.Value;

import dev.jlibra.serialization.bcs.BCS;

@Value.Immutable
public interface U64Argument extends TransactionArgument {

    @BCS.Field(0)
    long value();

    public static U64Argument from(long value) {
        return ImmutableU64Argument.builder()
                .value(value)
                .build();
    }

}
