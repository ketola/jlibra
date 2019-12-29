package dev.jlibra.admissioncontrol;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.google.common.util.concurrent.ListenableFuture;
import com.spotify.futures.ListenableFuturesExtra;

import admission_control.AdmissionControlGrpc;
import admission_control.AdmissionControlGrpc.AdmissionControlBlockingStub;
import admission_control.AdmissionControlGrpc.AdmissionControlFutureStub;
import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse;
import dev.jlibra.admissioncontrol.query.Query;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.SignedTransaction;
import dev.jlibra.admissioncontrol.transaction.result.LibraTransactionException;
import dev.jlibra.admissioncontrol.transaction.result.SubmitTransactionResult;
import io.grpc.Channel;
import types.GetWithProof.UpdateToLatestLedgerRequest;
import types.GetWithProof.UpdateToLatestLedgerResponse;

public class AdmissionControl {

    private Channel channel;

    public AdmissionControl(Channel channel) {
        this.channel = channel;
    }

    public SubmitTransactionResult submitTransaction(SignedTransaction transaction) throws LibraTransactionException {
        SubmitTransactionRequest request = transaction.toGrpcObject();
        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);
        SubmitTransactionResponse response = stub.submitTransaction(request);
        return SubmitTransactionResult.fromGrpcObject(response);
    }

    public CompletableFuture<SubmitTransactionResult> asyncSubmitTransaction(SignedTransaction transaction) {
        SubmitTransactionRequest request = transaction.toGrpcObject();
        AdmissionControlFutureStub stub = AdmissionControlGrpc.newFutureStub(channel);
        ListenableFuture<SubmitTransactionResponse> future = stub.submitTransaction(request);
        return ListenableFuturesExtra.toCompletableFuture(future)
                .thenApply(response -> {
                    try {
                        return SubmitTransactionResult.fromGrpcObject(response);
                    } catch (LibraTransactionException e) {
                        throw new CompletionException(e);
                    }
                });
    }

    public UpdateToLatestLedgerResult updateToLatestLedger(Query query) {
        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);
        UpdateToLatestLedgerResponse response = stub.updateToLatestLedger(UpdateToLatestLedgerRequest.newBuilder()
                .addAllRequestedItems(query.toGrpcObject())
                .build());
        return UpdateToLatestLedgerResult.fromGrpcObject(response);
    }

    public CompletableFuture<UpdateToLatestLedgerResult> asyncUpdateToLatestLedger(Query query) {
        AdmissionControlFutureStub stub = AdmissionControlGrpc.newFutureStub(channel);
        ListenableFuture<UpdateToLatestLedgerResponse> future = stub
                .updateToLatestLedger(UpdateToLatestLedgerRequest.newBuilder()
                        .addAllRequestedItems(query.toGrpcObject())
                        .build());

        return ListenableFuturesExtra.toCompletableFuture(future)
                .thenApply(UpdateToLatestLedgerResult::fromGrpcObject);
    }

    @Override
    public String toString() {
        return "AdmissionControl " + this.channel.toString();
    }
}
