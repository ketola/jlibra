package dev.jlibra.admissioncontrol.transaction;

import com.google.protobuf.ByteString;
import types.Transaction;

import java.nio.ByteBuffer;

import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static types.Transaction.TransactionArgument.ArgType.U64;

public class U64Argument implements TransactionArgument {

    private long value;

    public U64Argument(long value) {
        this.value = value;
    }

    @Override
    public byte[] toByteArray() {
        return ByteBuffer.allocate(Long.BYTES).order(LITTLE_ENDIAN).putLong(value)
                .order(LITTLE_ENDIAN).array();
    }

    @Override
    public Type type() {
        return Type.U64;
    }

    @Override
    public Transaction.TransactionArgument toGrpcTransactionArgument() {
        return Transaction.TransactionArgument.newBuilder()
                .setType(U64)
                .setData(ByteString.copyFrom(toByteArray()))
                .build();
    }
}
