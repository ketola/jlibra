package dev.jlibra.admissioncontrol.transaction.result;

import admission_control.AdmissionControlOuterClass.AdmissionControlStatusCode;

public class LibraAdmissionControlException extends SubmitTransactionException {

    public final AdmissionControlStatusCode statusCode;

    public LibraAdmissionControlException(AdmissionControlStatusCode statusCode) {
        super(String.format("Submit transaction failed with admission control status %s (%d)", statusCode.name(),
                statusCode.getNumber()));
        this.statusCode = statusCode;
    }

}
