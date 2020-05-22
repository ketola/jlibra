package dev.jlibra.transaction;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;
import dev.jlibra.transaction.argument.TransactionArgument;

@Value.Immutable
public interface Script extends TransactionPayload {

    @LCS.Field(0)
    ByteSequence getCode();

    @LCS.Field(1)
    List<TypeTag> getTypeArguments();

    @LCS.Field(2)
    List<TransactionArgument> getArguments();
}
