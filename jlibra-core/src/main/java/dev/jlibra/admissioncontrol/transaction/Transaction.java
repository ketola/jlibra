package dev.jlibra.admissioncontrol.transaction;

import static dev.jlibra.serialization.Deserialization.readByteSequence;
import static dev.jlibra.serialization.Deserialization.readInt;
import static dev.jlibra.serialization.Deserialization.readLong;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public abstract class Transaction implements LibraSerializable {

    public abstract AccountAddress getSenderAccount();

    public abstract long getSequenceNumber();

    public abstract Script getPayload();

    public abstract long getExpirationTime();

    public abstract long getGasUnitPrice();

    public abstract long getMaxGasAmount();

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .appendWithoutLengthInformation(getSenderAccount().getByteSequence())
                .appendLong(getSequenceNumber())
                .appendSerializable(getPayload())
                .appendLong(getMaxGasAmount())
                .appendLong(getGasUnitPrice())
                .appendLong(getExpirationTime())
                .toByteSequence();
    }

    public static Transaction fromGrpcObject(types.TransactionOuterClass.Transaction grpcTransaction) {
        byte[] bytes = grpcTransaction.getTransaction().toByteArray();

        InputStream in = new ByteArrayInputStream(bytes);
        int structPrefix = readInt(in, 4);
        AccountAddress senderAccount = AccountAddress.ofByteSequence(readByteSequence(in, 32));
        long sequenceNumber = readLong(in, 8);
        int payloadType = readInt(in, 4);

        if (payloadType != Script.PREFIX) {
            throw new LibraRuntimeException("Only Script payload is supported");
        }

        int codeLength = readInt(in, 4);
        ByteSequence code = readByteSequence(in, codeLength);
        int argumentsLength = readInt(in, 4);
        List<TransactionArgument> arguments = new ArrayList<>();
        for (int i = 0; i < argumentsLength; i++) {
            int argumentType = readInt(in, 4);

            if (argumentType == U64Argument.PREFIX) {
                long value = readLong(in, 8);
                arguments.add(new U64Argument(value));
            } else if (argumentType == AccountAddressArgument.PREFIX) {
                ByteSequence value = readByteSequence(in, 32);
                arguments.add(new AccountAddressArgument(value));
            } else if (argumentType == StringArgument.PREFIX) {
                int length = readInt(in, 4);
                String value = new String(readByteSequence(in, length).toArray(), UTF_8);
                arguments.add(new StringArgument(value));
            } else if (argumentType == ByteArrayArgument.PREFIX) {
                int length = readInt(in, 4);
                ByteSequence value = readByteSequence(in, length);
                arguments.add(new ByteArrayArgument(value));
            } else {
                throw new LibraRuntimeException("Unknown transaction argument type " + argumentType);
            }
        }

        long maxGasAmount = readLong(in, 8);
        long gasUnitPrice = readLong(in, 8);
        long expirationTime = readLong(in, 8);

        return ImmutableTransaction.builder()
                .senderAccount(senderAccount)
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

}
