package dev.jlibra.admissioncontrol.transaction;

import java.util.List;

import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public interface Program extends LibraSerializable {

    ByteString getCode();

    List<TransactionArgument> getArguments();

    default byte[] serialize() {
        int prefix = 0;
        return Serializer.builder()
                .appendInt(prefix)
                .appendByteArray(getCode().toByteArray())
                .appendTransactionArguments(getArguments())
                // modules are not supported yet, add 0 for empty array
                .appendInt(0)
                .toByteArray();
    }

}
