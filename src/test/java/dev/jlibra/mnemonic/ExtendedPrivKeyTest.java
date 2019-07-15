package dev.jlibra.mnemonic;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.charset.Charset;
import java.security.Security;

import static dev.jlibra.mnemonic.LibraKeyFactoryTest.TEST_MNEMONIC;
import static org.junit.Assert.*;

public class ExtendedPrivKeyTest {

    private static final byte[] TEST_MESSAGE_UTF8 =
            "Ḽơᶉëᶆ ȋṕšᶙṁ ḍỡḽǭᵳ ʂǐť ӓṁệẗ, ĉṓɲṩḙċťᶒțûɾ ấɖḯƥĭṩčįɳġ ḝłįʈ, șếᶑ ᶁⱺ ẽḭŭŝḿꝋď ṫĕᶆᶈṓɍ ỉñḉīḑȋᵭṵńť ṷŧ ḹẩḇőꝛế éȶ đꝍꞎôꝛȇ ᵯáꞡᶇā ąⱡîɋṹẵ.".getBytes(Charset.forName("UTF-8"));
    private static final byte[] TEST_MESSAGE_UTF8_DIFF =
            "Ḽơᶉëᶆ ȋṕšᶙṁ ḍỡḽǭᵳ ʂǐť ӓṁệẗ, ĉṓɲṩḙċťᶒțûɾ ấɖḯƥĭṩčįɳġ ḝłįʈ, șếᶑ ᶁⱺ ẽḭŭŝḿꝋď ṫĕᶆᶈṓɍ ỉñḉīḑȋᵭṵńť ṷŧ ḹẩḇőꝛế éȶ đꝍꞎôꝛȇ ᵯáꞡᶇā ąⱡîɋṹẵ,".getBytes(Charset.forName("UTF-8"));


    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    @Ignore("todo")
    public void getAddress() {
    }

    @Test
    @Ignore("todo")
    public void getPublic() {
        ExtendedPrivKey childPrivate0 = createExtendedPrivKey();

        assertEquals(
                "daae450c405afcad7fef66d3a7f7fae686247cf3064d7151d9a4a76ae0cace9a",
                childPrivate0.publicKey.toString()
        );
    }

    @Test
    @Ignore("todo")
    public void sign() {

    }

    @Test
    public void signAndVerifyMessageSuccessful() {
        ExtendedPrivKey childPrivate0 = createExtendedPrivKey();
        byte[] signedMessage = childPrivate0.signMessage(TEST_MESSAGE_UTF8);
        boolean isMessageSignedWithPrivateKey = childPrivate0.verifyMessage(TEST_MESSAGE_UTF8, signedMessage);
        assertTrue("signed message cannot be verified for key " + childPrivate0.publicKey.toString(), isMessageSignedWithPrivateKey);
    }

    @Test
    public void signAndVerifyMessageNotSuccessful() {
        ExtendedPrivKey childPrivate0 = createExtendedPrivKey();
        byte[] signedMessage = childPrivate0.signMessage(TEST_MESSAGE_UTF8);
        boolean isMessageSignedWithPrivateKey = childPrivate0.verifyMessage(TEST_MESSAGE_UTF8_DIFF, signedMessage);
        assertFalse("signed message cannot be verified for key " + childPrivate0.publicKey.toString(), isMessageSignedWithPrivateKey);
    }

    private ExtendedPrivKey createExtendedPrivKey() {
        Mnemonic mnemonic = Mnemonic.fromString(TEST_MNEMONIC);
        Seed seed = new Seed(mnemonic, "LIBRA");
        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);
        return libraKeyFactory.privateChild(new ChildNumber(0));
    }

}