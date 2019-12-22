package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import dev.jlibra.serialization.ByteSequence;
import types.AccountStateBlobOuterClass.AccountStateBlob;
import types.AccountStateBlobOuterClass.AccountStateWithProof;
import types.GetWithProof.GetAccountStateResponse;
import types.GetWithProof.ResponseItem;
import types.GetWithProof.UpdateToLatestLedgerResponse;

public class UpdateToLatestLedgerResultTest {

    private static final ByteSequence ACCOUNT_STATE_BYTES = ByteSequence
            .from("010000002100000001217da6c6b3e19f1825cfb2676daecce3bf3de03cf26647c78df00b371b25cc978e000000200000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5dc0c62d00000000000000030000000000000020000000e7753442710f04596279f6ea097c48782b0683e8b50071e8f2e34be523d7f2c5000000000000000020000000885ae34cf1f18d0c970b416afc7c5475f132511c5f3bbdce4af52ea09a0fd7030000000000000000");

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
                .getAccountResources();
        assertThat(accountStates, is(iterableWithSize(1)));
        assertThat(accountStates.get(0).getAuthenticationKey().toString(),
                is("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"));
        assertThat(accountStates.get(0).getBalanceInMicroLibras(), is(3000000L));
        assertThat(accountStates.get(0).getReceivedEvents().getCount(), is(3));
        assertThat(accountStates.get(0).getSentEvents().getCount(), is(0));
        assertThat(accountStates.get(0).getSequenceNumber(), is(0));
        assertThat(accountStates.get(0).getDelegatedWithdrawalCapability(), is(false));
        assertThat(accountStates.get(0).getDelegatedKeyRotationCapability(), is(false));
    }

}
