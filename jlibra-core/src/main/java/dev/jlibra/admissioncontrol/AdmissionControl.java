package dev.jlibra.admissioncontrol;

import static dev.jlibra.admissioncontrol.GrpcMapper.toSubmitTransactionRequest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import admission_control.AdmissionControlGrpc;
import admission_control.AdmissionControlGrpc.AdmissionControlBlockingStub;
import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import admission_control.AdmissionControlOuterClass.SubmitTransactionResponse;
import dev.jlibra.admissioncontrol.query.Query;
import dev.jlibra.admissioncontrol.query.UpdateToLatestLedgerResult;
import dev.jlibra.admissioncontrol.transaction.ImmutableSubmitTransactionResult;
import dev.jlibra.admissioncontrol.transaction.SubmitTransactionResult;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.mnemonic.ExtendedPrivKey;
import io.grpc.Channel;
import types.GetWithProof.RequestItem;
import types.GetWithProof.UpdateToLatestLedgerRequest;
import types.GetWithProof.UpdateToLatestLedgerResponse;

public class AdmissionControl {

    private Channel channel;

    public AdmissionControl(Channel channel) {
        this.channel = channel;
    }

    public SubmitTransactionResult submitTransaction(ExtendedPrivKey fromAccount, Transaction transaction) {
        SubmitTransactionRequest request = toSubmitTransactionRequest(fromAccount, transaction, fromAccount::sign);

        return submitTransaction(request);
    }

    public SubmitTransactionResult submitTransaction(PublicKey publicKey, PrivateKey privateKey, Transaction transaction) {
        SubmitTransactionRequest request = toSubmitTransactionRequest(publicKey, privateKey, transaction);

        return submitTransaction(request);
    }

    private SubmitTransactionResult submitTransaction(SubmitTransactionRequest request) {
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
