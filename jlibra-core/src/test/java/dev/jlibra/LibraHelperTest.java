package dev.jlibra;

import static org.bouncycastle.util.encoders.Hex.encode;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.security.PrivateKey;
import java.security.Security;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.protobuf.ByteString;

import dev.jlibra.admissioncontrol.transaction.ImmutableProgram;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;

public class LibraHelperTest {

    private static final String PRIVATE_KEY_HEX = "3051020101300506032b6570042204202b1115484c64c297179d4ec8aa660f09eeae900a1ba6f16423f82869a101c8e98121002e00f50d1ba024895c72a92cee1310dfafefcc826629c266a4c80b914772f82d";

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    @Test
    public void testSignTransaction() {
        PrivateKey privateKey = KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX);
        String signature = new String(
                encode(LibraHelper.signTransaction(ImmutableTransaction.builder()
                        .expirationTime(1L)
                        .gasUnitPrice(1L)
                        .maxGasAmount(1L)
                        .program(ImmutableProgram.builder().code(ByteString.copyFrom(new byte[] { 1 })).build())
                        .senderAccount(new byte[] { 1 })
                        .sequenceNumber(1L)
                        .build(), privateKey)));

        assertThat(signature, is(
                "6161e43e8b6dd4c3dbe8dd76e0d55d77c49677a5712e3ef2466d1b30eb7878116c5cc048d9a06a946985687917bcdb7e83938c26b52db47dc597f1c202678f07"));
    }
}
