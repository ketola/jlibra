package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;

public class StringArgument implements TransactionArgument {

    private static final int PREFIX = 2;

    private String value;

    public StringArgument(String value) {
        this.value = value;
    }

    @Override
    public ByteSequence serialize() {
        return Serializer.builder()
                .appendInt(PREFIX)
                .appendString(value)
                .toByteSequence();
    }

}
