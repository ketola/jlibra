package dev.jlibra.admissioncontrol;

public interface TransactionArgument {

    public enum Type {
        U64, ADDRESS
    }

    byte[] toByteArray();

    Type type();

}
