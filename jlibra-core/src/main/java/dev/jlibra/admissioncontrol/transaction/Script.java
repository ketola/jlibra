package dev.jlibra.admissioncontrol.transaction;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;
import dev.jlibra.serialization.lcs.type.TransactionPayload;

@Value.Immutable
@LCS.ExternallyTaggedEnumeration(TransactionPayload.Script)
public interface Script {

    @LCS.Field(0)
    ByteSequence getCode();

    @LCS.Field(1)
    List<TransactionArgument> getArguments();
}
