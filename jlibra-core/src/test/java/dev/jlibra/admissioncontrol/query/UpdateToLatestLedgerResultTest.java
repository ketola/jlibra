package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import dev.jlibra.serialization.ByteArray;
import types.AccountStateBlobOuterClass.AccountStateBlob;
import types.AccountStateBlobOuterClass.AccountStateWithProof;
import types.GetWithProof.GetAccountStateResponse;
import types.GetWithProof.ResponseItem;
import types.GetWithProof.UpdateToLatestLedgerResponse;

public class UpdateToLatestLedgerResultTest {

    private static final ByteArray ACCOUNT_STATE_BYTES = ByteArray
            .from("020000002100000001055ef663be675145dcee3def2c0f3fe13eb5e706e5c66e3da71b93c77a1b89f67e0000002000000097784bdb5fde351a3fa8e400265989a34e03aec69589026b4a095c9cd2e53ca6000002000000000000001800000000000000000000004e03aec69589026b4a095c9cd2e53ca605000000000000001800000001000000000000004e03aec69589026b4a095c9cd2e53ca6080000000000000002000000000000002100000001371112ddabc639f77e8e7eae94c415e914104bc8272332e53f7d0f117b93234f08000000c0e1e40000000000");

    @Test
    public void testReadAccountStates() {
        UpdateToLatestLedgerResponse grpcResponse = UpdateToLatestLedgerResponse.newBuilder()
                .addResponseItems(ResponseItem.newBuilder()
                        .setGetAccountStateResponse(GetAccountStateResponse.newBuilder()
                                .setAccountStateWithProof(AccountStateWithProof.newBuilder().setBlob(
                                        AccountStateBlob.newBuilder()
                                                .setBlob(ACCOUNT_STATE_BYTES.toByteString())
                                                .build()))
                                .build()))
                .build();

        List<AccountResource> accountStates = UpdateToLatestLedgerResult.fromGrpcObject(grpcResponse)
                .getAccountStateQueryResults();
        assertThat(accountStates, is(iterableWithSize(1)));
        assertThat(accountStates.get(0).getAuthenticationKey().toString(),
                is("97784bdb5fde351a3fa8e400265989a34e03aec69589026b4a095c9cd2e53ca6"));
        assertThat(accountStates.get(0).getBalanceInMicroLibras(), is(15000000L));
        assertThat(accountStates.get(0).getReceivedEvents().getCount(), is(2));
        assertThat(accountStates.get(0).getSentEvents().getCount(), is(5));
        assertThat(accountStates.get(0).getSequenceNumber(), is(8));
        assertThat(accountStates.get(0).getDelegatedWithdrawalCapability(), is(false));
        assertThat(accountStates.get(0).getDelegatedKeyRotationCapability(), is(false));
    }

}
