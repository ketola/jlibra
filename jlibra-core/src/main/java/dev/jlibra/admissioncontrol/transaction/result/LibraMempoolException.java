package dev.jlibra.admissioncontrol.transaction.result;

import mempool_status.MempoolStatus.MempoolAddTransactionStatusCode;

public class LibraMempoolException extends LibraTransactionException {

    public final MempoolAddTransactionStatusCode statusCode;

    public LibraMempoolException(MempoolAddTransactionStatusCode statusCode, String message) {
        super(String.format("Submit transaction failed with mempool status %s (%d), message: %s", statusCode.name(),
                statusCode.getNumber(), message));
        this.statusCode = statusCode;
    }

}
