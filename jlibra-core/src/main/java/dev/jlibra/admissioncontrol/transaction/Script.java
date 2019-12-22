package dev.jlibra.admissioncontrol.transaction;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public interface Script extends LibraSerializable {

    int PREFIX = 2;

    ByteSequence getCode();

    List<TransactionArgument> getArguments();

    default ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .append(getCode())
                .appendTransactionArguments(getArguments())
                .toByteSequence();
    }

}
