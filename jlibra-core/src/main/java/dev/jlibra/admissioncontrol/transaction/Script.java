package dev.jlibra.admissioncontrol.transaction;

import java.util.List;

import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public interface Script extends LibraSerializable {

    static final int PREFIX = 2;

    ByteString getCode();

    List<TransactionArgument> getArguments();

    default byte[] serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendByteArray(getCode().toByteArray())
                .appendTransactionArguments(getArguments())
                .toByteArray();
    }

}
