package dev.jlibra.admissioncontrol.query;

public class GetAccountState {
    private byte[] address;

    public GetAccountState(byte[] address) {
        this.address = address;
    }

    public byte[] getAddress() {
        return address;
    }
}
