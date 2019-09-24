package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.LibraSerializable;

public interface TransactionArgument extends LibraSerializable {

    enum Type {
        U64, ADDRESS, STRING
    }

    Type type();
}
