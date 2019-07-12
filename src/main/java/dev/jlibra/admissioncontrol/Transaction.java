package dev.jlibra.admissioncontrol;

public class Transaction {

    private long sequenceNumber;

    private Program program;

    public Transaction(long sequenceNumber, Program program) {
        this.sequenceNumber = sequenceNumber;
        this.program = program;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public Program getProgram() {
        return program;
    }

}
