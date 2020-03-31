package dev.jlibra.admissioncontrol.transaction;

import static dev.jlibra.serialization.Deserialization.readByteArray;
import static dev.jlibra.serialization.Deserialization.readInt;
import static dev.jlibra.serialization.Deserialization.readLong;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.Serializer;
import dev.jlibra.serialization.lcs.LCS;
import dev.jlibra.serialization.lcs.type.TransactionPayload;

@Value.Immutable
@LCS.Structure
public interface Transaction {

    @LCS.Field(value = 0, fixedLength = true)
    AccountAddress getSenderAccount();

    @LCS.Field(1)
    long getSequenceNumber();

    @LCS.Field(2)
    Script getPayload();

    @LCS.Field(3)
    long getMaxGasAmount();

    @LCS.Field(4)
    long getGasUnitPrice();

    @LCS.Field(5)
    default LbrTypeTag getGasSpecifier() {
        return new LbrTypeTag();
    }

    @LCS.Field(6)
    long getExpirationTime();

    public static Transaction fromGrpcObject(types.TransactionOuterClass.Transaction grpcTransaction) {
        byte[] bytes = grpcTransaction.getTransaction().toByteArray();

        InputStream in = new ByteArrayInputStream(bytes);
        int structPrefix = readInt(in, 4);

        ByteArray senderAccount = readByteArray(in, 32);
        long sequenceNumber = readLong(in, 8);
        int payloadType = readInt(in, 4);

        if (payloadType != TransactionPayload.Script) {
            throw new LibraRuntimeException("Only Script payload is supported");
        }

        int codeLength = readInt(in, 4);
        ByteArray code = readByteArray(in, codeLength);
        int argumentsLength = readInt(in, 4);
        List<TransactionArgument> arguments = new ArrayList<>();
        for (int i = 0; i < argumentsLength; i++) {
            int argumentType = readInt(in, 4);
            if (argumentType == U64Argument.PREFIX) {
                long value = readLong(in, 8);
                arguments.add(new U64Argument(value));
            } else if (argumentType == AccountAddressArgument.PREFIX) {
                ByteArray value = readByteArray(in, 32);
                arguments.add(new AccountAddressArgument(AccountAddress.fromByteArray(value)));
            } else if (argumentType == ByteArrayArgument.PREFIX) {
                int length = readInt(in, 4);
                ByteArray value = readByteArray(in, length);
                arguments.add(new ByteArrayArgument(value));
            } else {
                throw new LibraRuntimeException("Unknown transaction argument type " + argumentType);
            }
        }

        long maxGasAmount = readLong(in, 8);
        long gasUnitPrice = readLong(in, 8);
        long expirationTime = readLong(in, 8);

        return ImmutableTransaction.builder()
                // .senderAccount(AccountAddress.fromByteArray(senderAccount))
                .sequenceNumber(sequenceNumber)
                .payload(ImmutableScript.builder()
                        .code(code)
                        .arguments(arguments)
                        .build())
                .expirationTime(expirationTime)
                .gasUnitPrice(gasUnitPrice)
                .maxGasAmount(maxGasAmount)
                .build();
    }

    public static final int STRUCT_TAG_TYPE_PARAMS_LENGTH = 0;
    public static final String STRUCT_TAG_MODULE = "T";
    public static final String STRUCT_TAG_ADDRESS = "LBR";
    public static final String STRUCT_TAG_ACCOUNT_ADDRESS = "0000000000000000000000000000000000000000000000000000000000000000";
    public static final byte RESOURCE_TAG = 1;

    public static void ss() {
        ByteArray serializedStructTag = Serializer.builder()
                .appendFixedLength(ByteArray.from(STRUCT_TAG_ACCOUNT_ADDRESS))
                .appendString(STRUCT_TAG_ADDRESS)
                .appendString(STRUCT_TAG_MODULE)
                .appendInt(STRUCT_TAG_TYPE_PARAMS_LENGTH)
                .toByteArray();
    }

}
