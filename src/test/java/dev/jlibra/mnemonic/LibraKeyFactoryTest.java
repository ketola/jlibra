package dev.jlibra.mnemonic;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test data copied from https://github.com/libra/libra/blob/master/client/libra_wallet/src/key_factory.rs
 */
public class LibraKeyFactoryTest {

    private static final String TEST_MNEMONIC = "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will";

    @Test
    public void hasherTest() {
        SHA3.Digest256 sha3 = new SHA3.Digest256();
        byte[] digest = sha3.digest("abc".getBytes());
        assertEquals(
                "3a985da74fe225b2045c172d6bd390bd855f086e3e9d525b46bfe24511431532",
                Hex.toHexString(digest)
        );
    }

    @Test
    public void fromSeed() {
        Mnemonic mnemonic = Mnemonic.fromString(TEST_MNEMONIC);
        Seed seed = new Seed(mnemonic, "LIBRA");

        assertEquals(
                "8d8d9b85e36b2b9486becd31288e9dc2501cf77f95deb7d141eeb49d77f8a80f",
                Hex.toHexString(seed.getData())
        );

        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);

        assertEquals(
                "16274c9618ed59177ca948529c1884ba65c57984d562ec2b4e5aa1ee3e3903be",
                Hex.toHexString(libraKeyFactory.getMaster().getData())
        );
    }

    @Test
    public void privateChild() {
        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(Seed.fromHex(
                "16274c9618ed59177ca948529c1884ba65c57984d562ec2b4e5aa1ee3e3903be"
        ));

        // Check child_0 key derivation.
        ExtendedPrivKey childPrivate0 = libraKeyFactory.privateChild(new ChildNumber(0));
        assertEquals(
                "358a375f36d74c30b7f3299b62d712b307725938f8cc931100fbd10a434fc8b9",
                Hex.toHexString(childPrivate0.privateKey.getData())
        );

        // Check determinism, regenerate child_0.
        ExtendedPrivKey childPrivate0_again = libraKeyFactory.privateChild(new ChildNumber(0));
        assertEquals(
                Hex.toHexString(childPrivate0.privateKey.getData()),
                Hex.toHexString(childPrivate0_again.privateKey.getData())
        );

        // Check child_1 key derivation.
        ExtendedPrivKey childPrivate1 = libraKeyFactory.privateChild(new ChildNumber(1));
        assertEquals(
                "a325fe7d27b1b49f191cc03525951fec41b6ffa2d4b3007bb1d9dd353b7e56a6",
                Hex.toHexString(childPrivate1.privateKey.getData())
        );

        ChildNumber child1Again = new ChildNumber(0);
        child1Again.increment();
        assertEquals(new ChildNumber(1), child1Again);

        // Check determinism, regenerate child_1, but by incrementing ChildNumber(0).
        ExtendedPrivKey childPrivate1FromIncrement = libraKeyFactory.privateChild(child1Again);
        assertEquals(
                "a325fe7d27b1b49f191cc03525951fec41b6ffa2d4b3007bb1d9dd353b7e56a6",
                Hex.toHexString(childPrivate1FromIncrement.privateKey.getData())
        );
    }
}