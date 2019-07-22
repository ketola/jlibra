package dev.jlibra.admissioncontrol.transaction;

import types.Transaction;

public interface TransactionArgument {

    enum Type {
        U64, ADDRESS
    }

    byte[] toByteArray();

    Type type();

    Transaction.TransactionArgument toGrpcTransactionArgument();

}
