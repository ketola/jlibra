package dev.jlibra.admissioncontrol;

import admission_control.AdmissionControlOuterClass.AdmissionControlStatus;
import mempool.MempoolStatus;
import types.VmErrors.VMStatus;

public class Result {

    // TODO: Create own enum types instead of putting grpc enums directly here
    // Could also add the description texts for clearer errors

    private AdmissionControlStatus admissionControlStatus;

    private MempoolStatus.MempoolAddTransactionStatus mempoolStatus;

    private VMStatus vmStatus;

    public Result(AdmissionControlStatus admissionControlStatus,
            MempoolStatus.MempoolAddTransactionStatus mempoolStatus,
            VMStatus vmStatus) {
        this.admissionControlStatus = admissionControlStatus;
        this.mempoolStatus = mempoolStatus;
        this.vmStatus = vmStatus;
    }

    public AdmissionControlStatus getAdmissionControlStatus() {
        return admissionControlStatus;
    }

    public MempoolStatus.MempoolAddTransactionStatus getMempoolStatus() {
        return mempoolStatus;
    }

    public VMStatus getVmStatus() {
        return vmStatus;
    }

}
