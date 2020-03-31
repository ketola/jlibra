package dev.jlibra.admissioncontrol.transaction.result;

public class LibraMempoolException extends LibraTransactionException {

    public final Long statusCode;

    public LibraMempoolException(Long statusCode, String message) {
        super(String.format("Submit transaction failed with mempool status %d, message: %s",
                statusCode, message));
        this.statusCode = statusCode;
    }

}
