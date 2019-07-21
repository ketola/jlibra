package dev.jlibra.admissioncontrol.transaction;

import admission_control.AdmissionControlOuterClass;
import org.immutables.value.Value;

import admission_control.AdmissionControlOuterClass.AdmissionControlStatus;
import mempool.MempoolStatus;
import types.VmErrors.VMStatus;

@Value.Immutable
public interface SubmitTransactionResult {

    // TODO: Create own enum types instead of putting grpc enums directly here
    // Could also add the description texts for clearer errors

    AdmissionControlStatus getAdmissionControlStatus();

    MempoolStatus.MempoolAddTransactionStatus getMempoolStatus();

    VMStatus getVmStatus();

    AdmissionControlOuterClass.SubmitTransactionResponse.StatusCase getStatusCase();

}
