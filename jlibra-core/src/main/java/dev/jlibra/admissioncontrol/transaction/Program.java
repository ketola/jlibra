package dev.jlibra.admissioncontrol.transaction;

import static dev.jlibra.serialization.CanonicalSerialization.join;
import static dev.jlibra.serialization.CanonicalSerialization.serializeByteArray;
import static dev.jlibra.serialization.CanonicalSerialization.serializeInt;
import static dev.jlibra.serialization.CanonicalSerialization.serializeTransactionArguments;

import java.util.List;

import org.bouncycastle.util.encoders.Hex;
import org.immutables.value.Value;

import com.google.protobuf.ByteString;

@Value.Immutable
public interface Program {

    ByteString getCode();

    List<TransactionArgument> getArguments();

    default byte[] serialize() {
        byte[] prefix = Hex.decode("00000000");
        byte[] result = prefix;
        result = join(result, serializeByteArray(getCode().toByteArray()));
        result = join(result, serializeTransactionArguments(getArguments()));
        // modules are not supported yet, add 0 for empty array
        result = join(result, serializeInt(0));
        return result;
    }

}
