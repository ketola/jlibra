package dev.jlibra.admissioncontrol.transaction;

public class Transaction {

    private long sequenceNumber;

    private Program program;

    private long expirationTime;

    private long gasUnitPrice;

    private long maxGasAmount;

    private Transaction() {
    }

    public static Transaction create() {
        return new Transaction();
    }

    public Transaction withProgram(Program program) {
        this.program = program;
        return this;
    }

    public Transaction withSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
        return this;
    }

    public Transaction withExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public Transaction withGasUnitPrice(long gasUnitPrice) {
        this.gasUnitPrice = gasUnitPrice;
        return this;
    }

    public Transaction withMaxGasAmount(long maxGasAmount) {
        this.maxGasAmount = maxGasAmount;
        return this;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public Program getProgram() {
        return program;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public long getGasUnitPrice() {
        return gasUnitPrice;
    }

    public long getMaxGasAmount() {
        return maxGasAmount;
    }

}
