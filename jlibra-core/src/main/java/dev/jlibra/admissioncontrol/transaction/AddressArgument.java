package dev.jlibra.admissioncontrol.transaction;

import com.google.protobuf.ByteString;
import types.Transaction;

import static types.Transaction.TransactionArgument.ArgType.ADDRESS;

public class AddressArgument implements TransactionArgument {

    private byte[] address;

    public AddressArgument(byte[] address) {
        this.address = address;
    }

    @Override
    public byte[] toByteArray() {
        return address;
    }

    @Override
    public Type type() {
        return Type.ADDRESS;
    }

    @Override
    public types.Transaction.TransactionArgument toGrpcTransactionArgument() {
        return Transaction.TransactionArgument.newBuilder()
                .setType(ADDRESS)
                .setData(ByteString.copyFrom(toByteArray()))
                .build();
    }
}
