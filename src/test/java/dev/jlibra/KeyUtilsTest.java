package dev.jlibra;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.encoders.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyUtilsTest {

    private static final String PRIVATE_KEY_HEX = "3051020101300506032b6570042204206dadf7a252c0e74add2e545a1e3c811f1f4bdd88f8c5e0080e068f4df6d909128121000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021";
    private static final String PUBLIC_KEY_HEX = "302a300506032b65700321000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021";

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void testToLibraAddress() throws Exception {
        PublicKey publicKey = getKeyFactory().generatePublic(new X509EncodedKeySpec(Hex
                .decode(PUBLIC_KEY_HEX)));

        assertThat(KeyUtils.toHexStringLibraAddress(publicKey.getEncoded()),
                is("eb99fc3808a8e439c58f87935cbe6774e4cc83459b463ea0813b34ef96f0ba87"));
    }

    @Test
    public void testPrivateKeyFromHexString() throws Exception {
        PrivateKey privateKey = getKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(Hex.decode(
                PRIVATE_KEY_HEX)));

        assertThat(privateKey, equalTo(KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX)));
    }

    @Test
    public void testPublicKeyFromHexString() throws Exception {
        PublicKey publicKey = getKeyFactory().generatePublic(new X509EncodedKeySpec((Hex.decode(
                PUBLIC_KEY_HEX))));

        assertThat(publicKey, equalTo(KeyUtils.publicKeyFromHexString(PUBLIC_KEY_HEX)));
    }

    private static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not get KeyFactory", e);
        }
    }
}
