package dev.jlibra.mnemonic;

import static org.junit.Assert.assertEquals;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.Security;

import dev.jlibra.KeyUtils;

/**
 * Test data and mnemonic seed generated using libra cli.
 */
public class ExtendedPrivKeyTest {

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    private ExtendedPrivKey childPrivate0;

    @Before
    public void setUp() {
        Mnemonic mnemonic = Mnemonic.fromString(
                "aim layer grit goat orchard daring lady work dice lottery tent virus push heavy hello endless inner bread cliff brick swallow general method walnut");
        Seed seed = new Seed(mnemonic, "LIBRA");
        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);
        childPrivate0 = libraKeyFactory.privateChild(new ChildNumber(0));
    }

    @Test
    public void getAddress() {
        assertEquals(
                "9263e21488ea4742c54de0d961c94743a01a974c6f095d8710f83044f0408ae7",
                childPrivate0.getAddress());
    }

    @Test
    public void getPublic() {
        assertEquals(
                "be10d382d1f3de00c19607f667b5b127da22f42f0a3a4b70eaef690365421511",
                Hex.toHexString(KeyUtils.stripPublicKeyPrefix(childPrivate0.publicKey.getEncoded())));
    }

}