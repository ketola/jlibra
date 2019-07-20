package dev.jlibra.admissioncontrol.transaction;

import admission_control.AdmissionControlOuterClass.AdmissionControlStatus;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse.StatusCase;
import mempool.MempoolStatus.MempoolAddTransactionStatus;
import org.immutables.value.Value;
import types.VmErrors.VMStatus;

@Value.Immutable
public interface SubmitTransactionResult {

    // TODO: Create own enum types instead of putting grpc enums directly here
    // Could also add the description texts for clearer errors

    AdmissionControlStatus getAdmissionControlStatus();

    MempoolAddTransactionStatus getMempoolStatus();

    VMStatus getVmStatus();

    StatusCase getStatusCase();
}
