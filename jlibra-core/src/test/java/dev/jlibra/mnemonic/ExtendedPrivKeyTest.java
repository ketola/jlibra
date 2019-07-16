package dev.jlibra.mnemonic;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.security.Security;

import static dev.jlibra.mnemonic.LibraKeyFactoryTest.TEST_MNEMONIC;
import static org.junit.Assert.assertEquals;

public class ExtendedPrivKeyTest {

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
        Mnemonic mnemonic = Mnemonic.fromString(TEST_MNEMONIC);
        Seed seed = new Seed(mnemonic, "LIBRA");
        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);

        // Check child_0 key derivation.
        ExtendedPrivKey childPrivate0 = libraKeyFactory.privateChild(new ChildNumber(0));

        assertEquals(
                "daae450c405afcad7fef66d3a7f7fae686247cf3064d7151d9a4a76ae0cace9a",
                childPrivate0.publicKey.toString()
        );
    }

    @Test
    @Ignore("todo")
    public void sign() {
    }
}