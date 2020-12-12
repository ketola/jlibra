package dev.jlibra.transaction;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.dcs.DCS;
import dev.jlibra.transaction.argument.TransactionArgument;

@Value.Immutable
public interface Script extends TransactionPayload {

    @DCS.Field(0)
    ByteSequence getCode();

    @DCS.Field(1)
    List<TypeTag> getTypeArguments();

    @DCS.Field(2)
    List<TransactionArgument> getArguments();
}
