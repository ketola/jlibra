package dev.jlibra;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

public class MultisignaturePublicKeyTest {

    private static final String PUBKEY1 = "302a300506032b6570032100df2226d8633a4f392cca1c5d198ae10e99e1192b51b665d2d8ce06fa2896d3ae";
    private static final String PUBKEY2 = "302a300506032b6570032100c9a77a22407913ce2b0f4e360a228fdae8ca14db5055aa97296e828b7546bd36";
    private static final String PUBKEY3 = "302a300506032b6570032100892bd1625b22d4bc1b1700d9f8aa822a7ad97ffa380fdf765aae7ce97a234e6b";

    @Before
    public void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testCreate() {
        MultiSignaturePublicKey multisigPubKey = MultiSignaturePublicKey.create(asList(PublicKey.fromHexString(PUBKEY1),
                PublicKey.fromHexString(PUBKEY2), PublicKey.fromHexString(PUBKEY3)), 2);

        assertThat(multisigPubKey.toString(), is(
                "df2226d8633a4f392cca1c5d198ae10e99e1192b51b665d2d8ce06fa2896d3aec9a77a22407913ce2b0f4e360a228fdae8ca14db5055aa97296e828b7546bd36892bd1625b22d4bc1b1700d9f8aa822a7ad97ffa380fdf765aae7ce97a234e6b02"));
    }

}
