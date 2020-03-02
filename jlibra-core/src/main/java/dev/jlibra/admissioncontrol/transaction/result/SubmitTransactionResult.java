package dev.jlibra.admissioncontrol.transaction.result;

import org.immutables.value.Value;

import admission_control.AdmissionControlOuterClass.AdmissionControlStatusCode;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

/**
 * The only case where the transaction has been submitted successfully is when
 * the result from the grpc call is AdmissionControlStatus with status Accepted.
 * All other outcomes will result in an exception.
 * 
 */
@Value.Immutable
public abstract class SubmitTransactionResult {

    public abstract ByteSequence getValidatorId();

    public static SubmitTransactionResult fromGrpcObject(SubmitTransactionResponse response)
            throws LibraTransactionException {
        switch (response.getStatusCase()) {
        case AC_STATUS: {
            if (response.getAcStatus().getCode() == AdmissionControlStatusCode.Accepted) {
                return ImmutableSubmitTransactionResult.builder()
                        .validatorId(ByteArray.from(response.getValidatorId().toByteArray()))
                        .build();
            }
            throw new LibraAdmissionControlException(response.getAcStatus().getCode(),
                    response.getAcStatus().getMessage());
        }
        case MEMPOOL_STATUS:
            throw new LibraMempoolException(response.getMempoolStatus().getCode(),
                    response.getMempoolStatus().getMessage());
        case VM_STATUS:
            throw new LibraVirtualMachineException(response.getVmStatus().getMajorStatus(),
                    response.getVmStatus().getSubStatus(), response.getVmStatus().getMessage());
        default:
            throw new LibraTransactionException("Submit transaction failed with unknown status");
        }

    }
}
