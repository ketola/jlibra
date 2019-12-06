package dev.jlibra.admissioncontrol.transaction.result;

import org.immutables.value.Value;

import admission_control.AdmissionControlOuterClass.AdmissionControlStatusCode;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse;

/**
 * The only case where the transaction has been submitted successfully is when
 * the result from the grpc call is AdmissionControlStatus with status Accepted.
 * All other outcomes will result in an exception.
 * 
 */
@Value.Immutable
public abstract class SubmitTransactionResult {

    public abstract byte[] getValidatorId();

    public static SubmitTransactionResult fromGrpcObject(SubmitTransactionResponse response) {
        switch (response.getStatusCase()) {
        case AC_STATUS: {
            if (response.getAcStatus().getCode() == AdmissionControlStatusCode.Accepted) {
                return ImmutableSubmitTransactionResult.builder()
                        .validatorId(response.getValidatorId().toByteArray())
                        .build();
            }
            throw new LibraAdmissionControlException(response.getAcStatus().getCode());
        }
        case MEMPOOL_STATUS:
            throw new LibraMempoolException(response.getMempoolStatus().getCode());
        case VM_STATUS:
            throw new LibraVirtualMachineExcption(response.getVmStatus().getMajorStatus(),
                    response.getVmStatus().getSubStatus(), response.getVmStatus().getMessage());
        default:
            throw new SubmitTransactionException("Submit transaction failed with unkown status");
        }

    }
}
