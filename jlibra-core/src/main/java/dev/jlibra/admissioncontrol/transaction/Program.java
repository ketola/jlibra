package dev.jlibra.admissioncontrol.transaction;

import java.util.List;

import org.bouncycastle.util.encoders.Hex;
import org.immutables.value.Value;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.CanonicalSerialization;

@Value.Immutable
public interface Program {

    ByteString getCode();

    List<TransactionArgument> getArguments();

    default byte[] serialize() {

        byte[] prefix = Hex.decode("00000000");

        byte[] result = prefix;
        result = CanonicalSerialization.join(result,
                CanonicalSerialization
                        .serializeByteArray(getCode().toByteArray()));
        result = CanonicalSerialization.join(result,
                CanonicalSerialization.serializeTransactionArguments(getArguments()));
        result = CanonicalSerialization.join(result, CanonicalSerialization.serializeInt(0));
        return result;
    }

}
