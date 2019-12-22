package dev.jlibra.admissioncontrol.transaction.result;

import admission_control.AdmissionControlOuterClass.AdmissionControlStatus;
import admission_control.AdmissionControlOuterClass.AdmissionControlStatusCode;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse;
import dev.jlibra.serialization.ByteSequence;
import mempool_status.MempoolStatus.MempoolAddTransactionStatus;
import mempool_status.MempoolStatus.MempoolAddTransactionStatusCode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import types.VmErrors.VMStatus;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SubmitTransactionResultTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testAdmissionControlAccepted() throws Exception {
        ByteSequence validatorId = ByteSequence.from(new byte[] { 1 });
        SubmitTransactionResponse response = SubmitTransactionResponse.newBuilder()
                .setAcStatus(AdmissionControlStatus.newBuilder()
                        .setCode(AdmissionControlStatusCode.Accepted)
                        .build())
                .setValidatorId(validatorId.toByteString())
                .build();

        SubmitTransactionResult result = SubmitTransactionResult.fromGrpcObject(response);
        assertThat(result.getValidatorId(), is(validatorId));
    }

    @Test
    public void testAdmissionControlException() throws Exception {
        exception.expect(LibraAdmissionControlException.class);
        exception.expectMessage("Submit transaction failed with admission control status Rejected (2)");

        SubmitTransactionResponse response = SubmitTransactionResponse.newBuilder()
                .setAcStatus(AdmissionControlStatus.newBuilder()
                        .setCode(AdmissionControlStatusCode.Rejected)
                        .build())
                .build();

        SubmitTransactionResult.fromGrpcObject(response);
    }

    @Test
    public void testMempoolException() throws Exception {
        exception.expect(LibraMempoolException.class);
        exception.expectMessage("Submit transaction failed with mempool status InsufficientBalance (1)");

        SubmitTransactionResponse response = SubmitTransactionResponse.newBuilder()
                .setMempoolStatus(MempoolAddTransactionStatus.newBuilder()
                        .setCode(MempoolAddTransactionStatusCode.InsufficientBalance)
                        .build())
                .build();

        SubmitTransactionResult.fromGrpcObject(response);
    }

    @Test
    public void testVirtualMachineException() throws Exception {
        exception.expect(LibraVirtualMachineException.class);
        exception.expectMessage(
                "Submit transaction failed with virtual machine major status: 2, sub status: 3, message: some message (see https://github.com/libra/libra/blob/master/types/src/vm_error.rs#L260 for explanation for the code)");

        SubmitTransactionResponse response = SubmitTransactionResponse.newBuilder()
                .setVmStatus(VMStatus.newBuilder()
                        .setMajorStatus(2)
                        .setSubStatus(3)
                        .setMessage("some message")
                        .build())
                .build();

        SubmitTransactionResult.fromGrpcObject(response);
    }
}
