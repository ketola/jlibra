package dev.jlibra.transaction.argument;

import org.immutables.value.Value;

import dev.jlibra.serialization.bcs.BCS;

@Value.Immutable
public interface BoolArgument extends TransactionArgument {

    @BCS.Field(0)
    boolean value();

    public static BoolArgument from(boolean value) {
        return ImmutableBoolArgument.builder()
                .value(value)
                .build();
    }

}
