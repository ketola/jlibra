package dev.jlibra.admissioncontrol.transaction;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
@LCS.Enum(ordinal = 2)
public interface Script extends LibraSerializable {

    int PREFIX = 2;

    @LCS.Field(ordinal = 0)
    ByteSequence getCode();

    @LCS.Field(ordinal = 1)
    List<TransactionArgument> getArguments();

    default ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .append(getCode())
                .appendTransactionArguments(getArguments())
                .toByteSequence();
    }

}
