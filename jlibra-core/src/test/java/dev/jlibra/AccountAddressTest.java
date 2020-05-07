package dev.jlibra;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import dev.jlibra.serialization.ByteArray;

public class AccountAddressTest {

    private String AUTH_KEY = "39123ca11060fe90b0fd3a7133a2d0cc6e2eee74d379af9d0409df8ea15b1749";

    private String ACCOUNT_ADDRESS = "6e2eee74d379af9d0409df8ea15b1749";

    @Test
    public void testFromAuthenticationKey() {
        assertThat(AccountAddress.fromAuthenticationKey(AuthenticationKey.fromHexString(AUTH_KEY)).toString(),
                is(ACCOUNT_ADDRESS));
    }

    @Test
    public void testFromByteArray() {
        assertThat(AccountAddress.fromByteArray(ByteArray.from(Hex.decode(ACCOUNT_ADDRESS))).toString(),
                is(ACCOUNT_ADDRESS));
    }

    @Test
    public void testFromHexString() {
        assertThat(AccountAddress.fromHexString(ACCOUNT_ADDRESS).toString(),
                is(ACCOUNT_ADDRESS));
    }
}
