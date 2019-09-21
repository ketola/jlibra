package dev.jlibra.admissioncontrol.transaction;

import static dev.jlibra.serialization.CanonicalSerialization.join;
import static dev.jlibra.serialization.CanonicalSerialization.serializeByteArray;

import org.bouncycastle.util.encoders.Hex;

public class AccountAddressArgument implements TransactionArgument {

    private byte[] address;

    private static final byte[] PREFIX = Hex.decode("01000000");

    public AccountAddressArgument(byte[] address) {
        this.address = address;
    }

    @Override
    public byte[] serialize() {
        return join(PREFIX, serializeByteArray(address));
    }

    @Override
    public Type type() {
        return Type.ADDRESS;
    }
}
