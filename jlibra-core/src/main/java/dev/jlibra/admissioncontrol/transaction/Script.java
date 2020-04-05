package dev.jlibra.admissioncontrol.transaction;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
@LCS.Structure(builderClass = ImmutableScript.class)
public interface Script extends dev.jlibra.admissioncontrol.transaction.TransactionPayload {

    @LCS.Field(0)
    ByteArray code();

    @LCS.Field(1)
    List<TransactionArgument> arguments();
}
