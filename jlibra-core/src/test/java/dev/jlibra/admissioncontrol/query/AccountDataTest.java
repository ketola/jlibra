package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class AccountDataTest {

    private static final String ACCOUNT_DATA = "200000006674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c306588010000000000030000000000000020000000713683f27b7941f8178a11aa63e84df91f145778e4643916e444412eb6d6b0e5040000000000000020000000071f1ea79b401b3dc196a5814e11b0f52072c7a7d56fcdaa8f9d68f0022905550400000000000000";

    @Test
    public void testDeserialize() {
        byte[] accountDataBytes = Hex.decode(ACCOUNT_DATA);

        AccountData accountData = AccountData.deserialize(accountDataBytes);

        assertThat(Hex.toHexString(accountData.getAccountAddress()),
                is("6674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c"));
        assertThat(accountData.getBalanceInMicroLibras(), is(25716016L));
        assertThat(accountData.getReceivedEvents().getCount(), is(3));
        assertThat(accountData.getSentEvents().getCount(), is(4));
        assertThat(accountData.getSequenceNumber(), is(4));
        assertThat(accountData.getDelegatedWithdrawalCapability(), is(false));
    }
}
