package dev.jlibra.admissioncontrol.transaction;

import static dev.jlibra.serialization.Deserialization.readByteSequence;
import static dev.jlibra.serialization.Deserialization.readInt;
import static dev.jlibra.serialization.Deserialization.readLong;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.lcs.LCS;
import dev.jlibra.serialization.lcs.LCSSerializer;
import dev.jlibra.serialization.lcs.type.TransactionPayload;

@Value.Immutable
@LCS.Structure
public abstract class Transaction implements LibraSerializable {

    @LCS.Field(0)
    public abstract FixedLengthByteSequence getSenderAccount();

    @LCS.Field(1)
    public abstract long getSequenceNumber();

    @LCS.Field(2)
    public abstract Script getPayload();

    @LCS.Field(3)
    public abstract long getMaxGasAmount();

    @LCS.Field(4)
    public abstract long getGasUnitPrice();

    @LCS.Field(5)
    public abstract long getExpirationTime();

    public static Transaction fromGrpcObject(types.TransactionOuterClass.Transaction grpcTransaction) {
        byte[] bytes = grpcTransaction.getTransaction().toByteArray();

        InputStream in = new ByteArrayInputStream(bytes);
        int structPrefix = readInt(in, 4);

        FixedLengthByteSequence senderAccount = ImmutableFixedLengthByteSequence.builder()
                .value(readByteSequence(in, 32))
                .build();
        long sequenceNumber = readLong(in, 8);
        int payloadType = readInt(in, 4);

        if (payloadType != TransactionPayload.Script) {
            throw new LibraRuntimeException("Only Script payload is supported");
        }

        int codeLength = readInt(in, 4);
        VariableLengthByteSequence code = ImmutableVariableLengthByteSequence.builder()
                .value(readByteSequence(in, codeLength))
                .build();
        int argumentsLength = readInt(in, 4);
        List<TransactionArgument> arguments = new ArrayList<>();
        for (int i = 0; i < argumentsLength; i++) {
            int argumentType = readInt(in, 4);
            if (argumentType == U64Argument.PREFIX) {
                long value = readLong(in, 8);
                arguments.add(new U64Argument(value));
            } else if (argumentType == AccountAddressArgument.PREFIX) {
                ByteSequence value = readByteSequence(in, 32);
                arguments.add(new AccountAddressArgument(ImmutableFixedLengthByteSequence.builder()
                        .value(value)
                        .build()));
            } else if (argumentType == ByteArrayArgument.PREFIX) {
                int length = readInt(in, 4);
                ByteSequence value = readByteSequence(in, length);
                arguments.add(new ByteArrayArgument(ImmutableVariableLengthByteSequence.builder()
                        .value(value)
                        .build()));
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

    @Override
    public VariableLengthByteSequence serialize() {
        return new LCSSerializer().serialize(this, Transaction.class);
    }

}
