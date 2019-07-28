package dev.jlibra.mnemonic;

import static org.junit.Assert.assertEquals;

import java.security.Security;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test data copied from
 * https://github.com/libra/libra/blob/master/client/libra_wallet/src/key_factory.rs
 */
public class LibraKeyFactoryTest {

    static final String TEST_MNEMONIC = "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will";

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void hasherTest() {
        SHA3.Digest256 sha3 = new SHA3.Digest256();
        byte[] digest = sha3.digest("abc".getBytes());
        assertEquals(
                "3a985da74fe225b2045c172d6bd390bd855f086e3e9d525b46bfe24511431532",
                Hex.toHexString(digest));
    }

    @Test
    public void fromSeed() {
        Mnemonic mnemonic = Mnemonic.fromString(TEST_MNEMONIC);
        Seed seed = new Seed(mnemonic, "LIBRA");

        assertEquals(
                "8d8d9b85e36b2b9486becd31288e9dc2501cf77f95deb7d141eeb49d77f8a80f",
                Hex.toHexString(seed.getData()));

        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);

        assertEquals(
                "16274c9618ed59177ca948529c1884ba65c57984d562ec2b4e5aa1ee3e3903be",
                Hex.toHexString(libraKeyFactory.getMaster().getData()));
    }

    @Test
    public void privateChild() {
        Mnemonic mnemonic = Mnemonic.fromString(TEST_MNEMONIC);
        Seed seed = new Seed(mnemonic, "LIBRA");
        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);

        // Check child_0 key derivation.
        ExtendedPrivKey childPrivate0 = libraKeyFactory.privateChild(new ChildNumber(0));

        assertEquals(
                "3051020101300506032b657004220420358a375f36d74c30b7f3299b62d712b307725938f8cc931100fbd10a434fc8b9812100b1894672e853ee6d2f1c094b0db2c2644edd4736f46c4357c709fdded5797c44",
                Hex.toHexString(childPrivate0.privateKey.getEncoded()));

        // Check determinism, regenerate child_0.
        ExtendedPrivKey childPrivate0_again = libraKeyFactory.privateChild(new ChildNumber(0));
        assertEquals(
                Hex.toHexString(childPrivate0.privateKey.getEncoded()),
                Hex.toHexString(childPrivate0_again.privateKey.getEncoded()));

        // Check child_1 key derivation.
        ExtendedPrivKey childPrivate1 = libraKeyFactory.privateChild(new ChildNumber(1));
        assertEquals(
                "3051020101300506032b657004220420a325fe7d27b1b49f191cc03525951fec41b6ffa2d4b3007bb1d9dd353b7e56a6812100c66551332920eda683a977292305ef6c10c34b27d9fef331bbd851f5c74d09ee",
                Hex.toHexString(childPrivate1.privateKey.getEncoded()));

        ChildNumber child1Again = new ChildNumber(0).increment();
        assertEquals(new ChildNumber(1).data, child1Again.data);

        // Check determinism, regenerate child_1, but by incrementing ChildNumber(0).
        ExtendedPrivKey childPrivate1FromIncrement = libraKeyFactory.privateChild(child1Again);
        assertEquals(
                "3051020101300506032b657004220420a325fe7d27b1b49f191cc03525951fec41b6ffa2d4b3007bb1d9dd353b7e56a6812100c66551332920eda683a977292305ef6c10c34b27d9fef331bbd851f5c74d09ee",
                Hex.toHexString(childPrivate1FromIncrement.privateKey.getEncoded()));
    }
}