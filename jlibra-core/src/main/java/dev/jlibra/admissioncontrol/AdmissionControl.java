package dev.jlibra.admissioncontrol;

import admission_control.AdmissionControlGrpc;
import admission_control.AdmissionControlGrpc.AdmissionControlBlockingStub;
import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse;
import dev.jlibra.admissioncontrol.query.Query;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.ImmutableSubmitTransactionResult;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.SubmitTransactionResult;
import io.grpc.Channel;
import types.GetWithProof.UpdateToLatestLedgerRequest;
import types.GetWithProof.UpdateToLatestLedgerResponse;

public class AdmissionControl {

    private Channel channel;

    public AdmissionControl(Channel channel) {
        this.channel = channel;
    }

    public SubmitTransactionResult submitTransaction(SignedTransaction transaction) {
        SubmitTransactionRequest request = transaction.toGrpcObject();
        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);
        SubmitTransactionResponse response = stub.submitTransaction(request);
        return ImmutableSubmitTransactionResult.builder()
                .admissionControlStatus(response.getAcStatus())
                .mempoolStatus(response.getMempoolStatus())
                .vmStatus(response.getVmStatus())
                .statusCase(response.getStatusCase())
                .build();
    }

    public UpdateToLatestLedgerResult updateToLatestLedger(Query query) {
        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);
        UpdateToLatestLedgerResponse response = stub.updateToLatestLedger(UpdateToLatestLedgerRequest.newBuilder()
                .addAllRequestedItems(query.toGrpcObject())
                .build());
        return UpdateToLatestLedgerResult.fromGrpcObject(response);
    }

    @Override
    public String toString() {
        return "AdmissionControl " + this.channel.toString();
    }
}
