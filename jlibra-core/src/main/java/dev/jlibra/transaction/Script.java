package dev.jlibra.transaction;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.bcs.BCS;
import dev.jlibra.transaction.argument.TransactionArgument;

@Value.Immutable
public interface Script extends TransactionPayload {

    @BCS.Field(0)
    ByteSequence getCode();

    @BCS.Field(1)
    List<TypeTag> getTypeArguments();

    @BCS.Field(2)
    List<TransactionArgument> getArguments();
}
