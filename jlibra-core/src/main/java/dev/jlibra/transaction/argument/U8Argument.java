package dev.jlibra.transaction.argument;

import org.immutables.value.Value;

import dev.jlibra.serialization.lcs.DCS;

@Value.Immutable
public interface U8Argument extends TransactionArgument {

    @DCS.Field(0)
    byte value();

    public static U8Argument from(byte value) {
        return ImmutableU8Argument.builder()
                .value(value)
                .build();
    }

}
