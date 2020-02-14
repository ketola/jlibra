package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteSequence;
import types.AccountStateBlobOuterClass.AccountStateBlob;
import types.AccountStateBlobOuterClass.AccountStateWithProof;

public class AccountResourceTest {

    private static final ByteSequence ACCOUNT_DATA = ByteSequence
            .from("200000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5dc0c62d00000000000000030000000000000020000000e7753442710f04596279f6ea097c48782b0683e8b50071e8f2e34be523d7f2c5000000000000000020000000885ae34cf1f18d0c970b416afc7c5475f132511c5f3bbdce4af52ea09a0fd7030000000000000000");

    private static final ByteSequence ACCOUNT_STATE_BLOB = ByteSequence
            .from("0100000001000000018e000000200000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5dc0c62d00000000000000030000000000000020000000e7753442710f04596279f6ea097c48782b0683e8b50071e8f2e34be523d7f2c5000000000000000020000000885ae34cf1f18d0c970b416afc7c5475f132511c5f3bbdce4af52ea09a0fd7030000000000000000");

    @Test
    public void testDeserialize() {
        AccountResource accountData = AccountResource.deserialize(ACCOUNT_DATA);

        assertThat(accountData.getAuthenticationKey().toString(),
                is("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"));
        assertThat(accountData.getBalanceInMicroLibras(), is(3000000L));
        assertThat(accountData.getReceivedEvents().getCount(), is(3));
        assertThat(accountData.getSentEvents().getCount(), is(0));
        assertThat(accountData.getSequenceNumber(), is(0));
        assertThat(accountData.getDelegatedWithdrawalCapability(), is(false));
        assertThat(accountData.getDelegatedKeyRotationCapability(), is(false));
    }

    @Test
    public void fromGrpcObject() {
        AccountStateWithProof accountStateWithProof = AccountStateWithProof.newBuilder()
                .setBlob(AccountStateBlob.newBuilder().setBlob(ACCOUNT_STATE_BLOB.toByteString()))
                .build();
        assertThat(AccountResource.fromGrpcObject(accountStateWithProof).size(), is(1));
    }

    @Test
    public void fromGrpcObjectInvalidMessageFormat() {
        ByteSequence blob = ByteSequence
                .from("01000000200000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5dc0c62d00000000000000030000000000000020000000e7753442710f04596279f6ea097c48782b0683e8b50071e8f2e34be523d7f2c5000000000000000020000000885ae34cf1f18d0c970b416afc7c5475f132511c5f3bbdce4af52ea09a0fd7030000000000000000");

        AccountStateWithProof accountStateWithProof = AccountStateWithProof.newBuilder()
                .setBlob(AccountStateBlob.newBuilder().setBlob(blob.toByteString()))
                .build();
        try {
            AccountResource.fromGrpcObject(accountStateWithProof);
        } catch (LibraRuntimeException e) {
            assertThat(e.getMessage(), is(
                    "Message is not long enough (102) to read 3000000 bytes from it. It could mean the message is corrupted or different format than expected."));
        }
    }
}
