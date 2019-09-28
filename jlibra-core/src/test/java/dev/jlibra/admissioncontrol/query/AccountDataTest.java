package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class AccountDataTest {

    private static final String ACCOUNT_DATA = "200000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5dc0c62d00000000000000030000000000000020000000e7753442710f04596279f6ea097c48782b0683e8b50071e8f2e34be523d7f2c5000000000000000020000000885ae34cf1f18d0c970b416afc7c5475f132511c5f3bbdce4af52ea09a0fd7030000000000000000";

    @Test
    public void testDeserialize() {
        byte[] accountDataBytes = Hex.decode(ACCOUNT_DATA);

        AccountData accountData = AccountData.deserialize(accountDataBytes);

        assertThat(Hex.toHexString(accountData.getAccountAddress()),
                is("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"));
        assertThat(accountData.getBalanceInMicroLibras(), is(3000000L));
        assertThat(accountData.getReceivedEvents().getCount(), is(3));
        assertThat(accountData.getSentEvents().getCount(), is(0));
        assertThat(accountData.getSequenceNumber(), is(0));
        assertThat(accountData.getDelegatedWithdrawalCapability(), is(false));
        assertThat(accountData.getDelegatedKeyRotationCapability(), is(false));
    }
}
