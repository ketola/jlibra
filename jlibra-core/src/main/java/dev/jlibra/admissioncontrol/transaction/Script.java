package dev.jlibra.admissioncontrol.transaction;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.lcs.LCS;
import dev.jlibra.serialization.lcs.LCSSerializer;
import dev.jlibra.serialization.lcs.type.TransactionPayload;

@Value.Immutable
@LCS.ExternallyTaggedEnumeration(TransactionPayload.Script)
public interface Script extends LibraSerializable {

    @LCS.Field(0)
    VariableLengthByteSequence getCode();

    @LCS.Field(1)
    List<TransactionArgument> getArguments();

    default VariableLengthByteSequence serialize() {
        return new LCSSerializer().serialize(this, Script.class);
    }

}
