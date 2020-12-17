package dev.jlibra.transaction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.security.PrivateKey;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.jlibra.AccountAddress;
import dev.jlibra.KeyUtils;
import dev.jlibra.serialization.ByteArray;

public class DualAttestationTest {

    private static final ByteArray PRIVATE_KEY = ByteArray
            .from("3051020101300506032b6570042204206dadf7a252c0e74add2e545a1e3c811f1f4bdd88f8c5e0080e068f4df6d909128121000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021");

    @BeforeAll
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testDualAttestationSignature() {
        PrivateKey privateKey = KeyUtils.privateKeyFromByteSequence(PRIVATE_KEY);

        DualAttestation s = ImmutableDualAttestation.builder()
                .amount(10_000_000L)
                .payerAddress(AccountAddress.fromHexString("0c6bd0bf42d298aced9184de984bc6ed"))
                .metadata(ByteArray.from("metadata".getBytes()))
                .build();

        ByteArray signature = s.sign(privateKey);

        assertThat(Hex.toHexString(signature.toArray()), is(
                "21356359434e29f6fa74d990d74e4f24bb69f2722ff8d7a2c789b67d361e7a3f0853009772ca21e327243a18d921bfd450245538b01e54cf047010d2fd05750e"));
    }
}
