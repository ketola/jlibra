package dev.jlibra.admissioncontrol.transaction;

public interface TransactionArgument {

    enum Type {
        U64, ADDRESS, STRING
    }

    byte[] serialize();

    Type type();
}
