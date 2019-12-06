package dev.jlibra.admissioncontrol.transaction.result;

import mempool_status.MempoolStatus.MempoolAddTransactionStatusCode;

public class LibraMempoolException extends SubmitTransactionException {

    public final MempoolAddTransactionStatusCode statusCode;

    public LibraMempoolException(MempoolAddTransactionStatusCode statusCode) {
        super(String.format("Submit transaction failed with mempool status %s (%d)", statusCode.name(),
                statusCode.getNumber()));
        this.statusCode = statusCode;
    }

}
