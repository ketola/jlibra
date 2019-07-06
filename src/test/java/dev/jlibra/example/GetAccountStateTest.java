package dev.jlibra.example;

import admission_control.AdmissionControlGrpc;
import admission_control.AdmissionControlGrpc.AdmissionControlBlockingStub;
import com.google.protobuf.ByteString;
import dev.jlibra.AccountState;
import dev.jlibra.LibraHelper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.java.Log;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;
import types.GetWithProof.GetAccountStateRequest;
import types.GetWithProof.RequestItem;
import types.GetWithProof.UpdateToLatestLedgerRequest;
import types.GetWithProof.UpdateToLatestLedgerResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log
public class GetAccountStateTest {

    @Test
    public void shouldGetAccountState() {
        String address = "045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98";

        ManagedChannel channel = ManagedChannelBuilder.forAddress("ac.testnet.libra.org", 8000)
                .usePlaintext()
                .build();

        AdmissionControlBlockingStub stub = AdmissionControlGrpc.newBlockingStub(channel);

        GetAccountStateRequest getAccountStateRequest = GetAccountStateRequest.newBuilder()
                .setAddress(ByteString.copyFrom(Hex.decode(address)))
                .build();

        RequestItem requestItem = RequestItem.newBuilder()
                .setGetAccountStateRequest(getAccountStateRequest)
                .build();

        UpdateToLatestLedgerResponse response = stub.updateToLatestLedger(UpdateToLatestLedgerRequest.newBuilder()
                .addAllRequestedItems(Arrays.asList(requestItem))
                .build());

        List<AccountState> accountStates = new ArrayList<>();

        response.getResponseItemsList().forEach(responseItem -> {
            accountStates.addAll(LibraHelper.readAccountStates(responseItem.getGetAccountStateResponse()
                    .getAccountStateWithProof()));
        });

        accountStates.forEach(accountState -> {
            StringBuilder sbAccountState = new StringBuilder();
            sbAccountState.append(System.lineSeparator() + "Address:" + new String(Hex.encode(accountState.getAddress())) + System.lineSeparator());
            sbAccountState.append("Received events: " + accountState.getReceivedEvents() + System.lineSeparator());
            sbAccountState.append("Sent events: " + accountState.getSentEvents() + System.lineSeparator());
            sbAccountState.append("Balance (microLibras): " + accountState.getBalanceInMicroLibras() + System.lineSeparator());
            sbAccountState.append("Balance (Libras): "
                    + new BigDecimal(accountState.getBalanceInMicroLibras()).divide(BigDecimal.valueOf(1000000)) + System.lineSeparator());
            sbAccountState.append("Sequence number: " + accountState.getSequenceNumber() + System.lineSeparator() + System.lineSeparator());
            log.info(sbAccountState.toString());
        });
    }

}
