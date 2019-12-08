package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.security.PrivateKey;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.protobuf.ByteString;

import dev.jlibra.AccountAddress;
import dev.jlibra.KeyUtils;

public class SignatureTest {

    private static final String PRIVATE_KEY_HEX = "3051020101300506032b6570042204206dadf7a252c0e74add2e545a1e3c811f1f4bdd88f8c5e0080e068f4df6d909128121000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021";

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testSerialize() {
        Transaction transaction = ImmutableTransaction.builder()
                .expirationTime(1)
                .maxGasAmount(2)
                .gasUnitPrice(3)
                .sequenceNumber(4)
                .expirationTime(5L)
                .senderAccount(AccountAddress.ofByteArray(new byte[] { 1 }))
                .program(ImmutableProgram.builder()
                        .addArguments(new U64Argument(1000), new AccountAddressArgument(new byte[] { 2 }))
                        .code(ByteString.copyFrom(new byte[] { 3 }))
                        .build())
                .build();

        PrivateKey privateKey = KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX);
        Signature signature = ImmutableSignature.builder()
                .privateKey(privateKey)
                .transaction(transaction)
                .build();

        assertThat(Hex.toHexString(signature.signTransaction(transaction, privateKey)), is(
                "cdb05e40081406f4813b5a3122b3c09a5c6c5023c33b920d6d840b568b905281857d6e95582017423f6fd9721a2e40cfdccd6ba23eaf6426b87f4c62b4ee4b03"));
        assertThat(Hex.toHexString(signature.serialize()), is(
                "40000000cdb05e40081406f4813b5a3122b3c09a5c6c5023c33b920d6d840b568b905281857d6e95582017423f6fd9721a2e40cfdccd6ba23eaf6426b87f4c62b4ee4b03"));
    }

}
