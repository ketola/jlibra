package dev.jlibra;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.junit.BeforeClass;
import org.junit.Test;

import dev.jlibra.serialization.ByteArray;

public class KeyUtilsTest {

    private static final ByteArray PRIVATE_KEY = ByteArray
            .from("3051020101300506032b6570042204206dadf7a252c0e74add2e545a1e3c811f1f4bdd88f8c5e0080e068f4df6d909128121000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021");
    private static final ByteArray PUBLIC_KEY = ByteArray
            .from("302a300506032b65700321000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021");

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void testPrivateKeyFromHexString() throws Exception {
        PrivateKey privateKey = getKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(PRIVATE_KEY.toArray()));
        assertThat(privateKey, equalTo(KeyUtils.privateKeyFromByteSequence(PRIVATE_KEY)));
    }

    @Test
    public void testPublicKeyFromHexString() throws Exception {
        PublicKey publicKey = getKeyFactory().generatePublic(new X509EncodedKeySpec(PUBLIC_KEY.toArray()));
        assertThat(publicKey, equalTo(KeyUtils.publicKeyFromByteSequence(PUBLIC_KEY)));
    }

    private static KeyFactory getKeyFactory() throws Exception {
        return KeyFactory.getInstance("Ed25519");
    }
}
