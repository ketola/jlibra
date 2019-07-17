package dev.jlibra.admissioncontrol;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import admission_control.AdmissionControlGrpc;
import admission_control.AdmissionControlGrpc.AdmissionControlBlockingStub;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse;
import dev.jlibra.admissioncontrol.query.Query;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.SubmitTransactionResult;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import types.GetWithProof.RequestItem;
import types.GetWithProof.UpdateToLatestLedgerRequest;
import types.GetWithProof.UpdateToLatestLedgerResponse;

public class AdmissionControl {

    private String host;

    private int port;

    public AdmissionControl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public SubmitTransactionResult submitTransaction(PublicKey publicKey, PrivateKey privateKey,
            Transaction transaction) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);

        SubmitTransactionResponse response = stub
                .submitTransaction(GrpcMapper.toSubmitTransactionRequest(publicKey, privateKey, transaction));

        channel.shutdown();

        return new SubmitTransactionResult(response.getAcStatus(), response.getMempoolStatus(), response.getVmStatus());
    }

    public UpdateToLatestLedgerResult updateToLatestLedger(Query query) {

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);

        List<RequestItem> requestItems = new ArrayList<>();

        requestItems.addAll(GrpcMapper.accountStateQueriesToRequestItems(query.getAccountStateQueries()));

        requestItems.addAll(
                GrpcMapper.accountTransactionBySequenceNumberQueriesToRequestItems(
                        query.getAccountTransactionBySequenceNumberQueries()));

        UpdateToLatestLedgerResponse response = stub.updateToLatestLedger(UpdateToLatestLedgerRequest.newBuilder()
                .addAllRequestedItems(requestItems)
                .build());

        return GrpcMapper.updateToLatestLedgerResponseToResult(response);
    }
}
