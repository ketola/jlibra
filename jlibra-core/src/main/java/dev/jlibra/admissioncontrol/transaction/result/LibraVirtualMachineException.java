package dev.jlibra.admissioncontrol.transaction.result;

public class LibraVirtualMachineException extends SubmitTransactionException {

    public final long majorStatus;
    public final long subStatus;
    public final String message;

    public LibraVirtualMachineException(long majorStatus, long subStatus, String message) {
        super(String.format(
                "Submit transaction failed with virtual machine major status: %d, sub status: %d, message: %s (see https://github.com/libra/libra/blob/master/types/src/vm_error.rs#L260 for explanation for the code)",
                majorStatus, subStatus, message));
        this.majorStatus = majorStatus;
        this.subStatus = subStatus;
        this.message = message;
    }

}
