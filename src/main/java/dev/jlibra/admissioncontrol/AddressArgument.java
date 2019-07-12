package dev.jlibra.admissioncontrol;

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

}
