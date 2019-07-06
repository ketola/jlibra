package dev.jlibra;

import lombok.Getter;

@Getter
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

}
