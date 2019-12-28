package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;

public class AccountAddressArgument implements TransactionArgument {

    public static final int PREFIX = 1;

    private ByteSequence value;

    public AccountAddressArgument(ByteSequence address) {
        this.value = address;
    }

    public ByteSequence getValue() {
        return value;
    }

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendWithoutLengthInformation(value)
                .toByteSequence();
    }
}
