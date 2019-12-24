package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;

public class AccountAddressArgument implements TransactionArgument {

    private static final int PREFIX = 1;

    private ByteSequence address;

    public AccountAddressArgument(ByteSequence address) {
        this.address = address;
    }

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendWithoutLengthInformation(address)
                .toByteSequence();
    }
}
