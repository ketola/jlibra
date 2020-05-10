package dev.jlibra;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.security.Security;
import java.util.Arrays;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AuthenticationKeyTest {

    private String JAVA_PUB_KEY = "302a300506032b6570032100c4b04ece47684d7831b67e106f1552e35aee81b388c5c2fb285bf0e936933bd2";
    private String JAVA_PUB_KEY2 = "302a300506032b6570032100725a57c8320bb3b2d14acf5c02aac122e82d71b720847f95003dfe754740333a";

    private String AUTH_KEY = "14f98e36264b387e688ef7d4b18c718d2684e7f73e5784f08e03a9d7d96955f0";

    @Before
    public void setUp() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testFromJavaPublicKey() {
        java.security.PublicKey javaPublicKey = Mockito.mock(java.security.PublicKey.class);
        when(javaPublicKey.getEncoded()).thenReturn(Hex.decode(JAVA_PUB_KEY));
        assertThat(AuthenticationKey.fromPublicKey(javaPublicKey).toString(), is(AUTH_KEY));
    }

    @Test
    public void testFromPublicKey() {
        assertThat(AuthenticationKey.fromPublicKey(PublicKey.fromHexString(JAVA_PUB_KEY)).toString(), is(AUTH_KEY));
    }

    @Test
    public void testFromMultiSignaturePublicKey() {
        MultiSignaturePublicKey multisigPubKey = MultiSignaturePublicKey
                .create(Arrays.asList(PublicKey.fromHexString(JAVA_PUB_KEY), PublicKey.fromHexString(JAVA_PUB_KEY2)),
                        2);
        assertThat(AuthenticationKey.fromMultiSignaturePublicKey(multisigPubKey).toString(),
                is("5c4a01f38b5191171f838370e4675925eec2d9d8a4aad2c9d81257612371a680"));
    }

    @Test
    public void testPrefix() {
        assertThat(AuthenticationKey.fromHexString(AUTH_KEY).prefix().toString(),
                is("14f98e36264b387e688ef7d4b18c718d"));
    }
}