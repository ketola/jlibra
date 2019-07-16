package dev.jlibra.admissioncontrol.transaction;

public interface TransactionArgument {

    public enum Type {
        U64, ADDRESS
    }

    byte[] toByteArray();

    Type type();

}
