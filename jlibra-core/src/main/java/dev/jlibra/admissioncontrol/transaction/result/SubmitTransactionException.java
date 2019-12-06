package dev.jlibra.admissioncontrol.transaction.result;

import dev.jlibra.LibraRuntimeException;

public class SubmitTransactionException extends LibraRuntimeException {

    public SubmitTransactionException(String message) {
        super(message);
    }

}
