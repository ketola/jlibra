package dev.jlibra;

public class AccountState {
    private byte[] address;
    private long balanceInMicroLibras;
    private long receivedEvents;
    private long sentEvents;
    private long sequenceNumber;

    public AccountState(byte[] address, long balanceInMicroLibras, long receivedEvents, long sentEvents,
            long sequenceNumber) {
        this.address = address;
        this.balanceInMicroLibras = balanceInMicroLibras;
        this.receivedEvents = receivedEvents;
        this.sentEvents = sentEvents;
        this.sequenceNumber = sequenceNumber;
    }

    public byte[] getAddress() {
        return address;
    }

    public long getBalanceInMicroLibras() {
        return balanceInMicroLibras;
    }

    public long getReceivedEvents() {
        return receivedEvents;
    }

    public long getSentEvents() {
        return sentEvents;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

}
