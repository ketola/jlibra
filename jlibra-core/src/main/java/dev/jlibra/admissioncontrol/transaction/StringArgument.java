package dev.jlibra.admissioncontrol.transaction;

import static dev.jlibra.serialization.CanonicalSerialization.join;

import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.serialization.CanonicalSerialization;

public class StringArgument implements TransactionArgument {

    private static final byte[] PREFIX = Hex.decode("02000000");

    private String value;

    public StringArgument(String value) {
        this.value = value;
    }

    @Override
    public byte[] serialize() {
        return join(PREFIX, CanonicalSerialization.serializeString(value));
    }

    @Override
    public Type type() {
        return Type.STRING;
    }

}
