package dev.jlibra.admissioncontrol.query;

public class GetAccountTransactionBySequenceNumber {

    private byte[] accountAddress;
    private long sequenceNumber;

    public GetAccountTransactionBySequenceNumber(byte[] accountAddress, long sequenceNumber) {
        this.accountAddress = accountAddress;
        this.sequenceNumber = sequenceNumber;
    }

    public byte[] getAccountAddress() {
        return accountAddress;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

}
