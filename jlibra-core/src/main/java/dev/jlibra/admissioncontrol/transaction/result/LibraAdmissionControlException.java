package dev.jlibra.admissioncontrol.transaction.result;

import admission_control.AdmissionControlOuterClass.AdmissionControlStatusCode;

public class LibraAdmissionControlException extends LibraTransactionException {

    public final AdmissionControlStatusCode statusCode;

    public LibraAdmissionControlException(AdmissionControlStatusCode statusCode, String message) {
        super(String.format("Submit transaction failed with admission control status %s (%d), message: %s",
                statusCode.name(),
                statusCode.getNumber(), message));
        this.statusCode = statusCode;
    }

}
