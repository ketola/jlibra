package dev.jlibra.mnemonic;

import static org.junit.Assert.assertEquals;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.jlibra.serialization.ByteArray;

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
                "5551702ff5dbf7485255e0816708b8a2",
                childPrivate0.getAddress().toString());
    }

    @Test
    public void getPublic() {
        assertEquals(
                "be10d382d1f3de00c19607f667b5b127da22f42f0a3a4b70eaef690365421511",
                ByteArray.from(childPrivate0.publicKey.getEncoded()).subseq(12, 32).toString());
    }

}
