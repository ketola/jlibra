package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.Serializer;

public class AccountAddressArgument implements TransactionArgument {

    private static final int PREFIX = 1;

    private byte[] address;

    public AccountAddressArgument(byte[] address) {
        this.address = address;
    }

    @Override
    public byte[] serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendByteArray(address)
                .toByteArray();
    }

    @Override
    public Type type() {
        return Type.ADDRESS;
    }
}
