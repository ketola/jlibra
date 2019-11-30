package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.protobuf.ByteString;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import dev.jlibra.KeyUtils;

public class SignedTransactionTest {
    private static final String PUBLIC_KEY_HEX = "302a300506032b65700321000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021";

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
                .senderAccount(new byte[] { 1 })
                .program(ImmutableProgram.builder()
                        .addArguments(new U64Argument(1000), new AccountAddressArgument(new byte[] { 2 }))
                        .code(ByteString.copyFrom(new byte[] { 3 }))
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(KeyUtils.publicKeyFromHexString(PUBLIC_KEY_HEX))
                .signature(new byte[] { 5 })
                .transaction(transaction)
                .build();

        assertThat(Hex.toHexString(signedTransaction.serialize()).toUpperCase(), is(
                "0104000000000000000000000001000000030200000000000000E803000000000000010000000200000000020000000000000003000000000000000500000000000000200000000B29A7ADCE0897B2D1EC18CC482237463EFA173945FA3BD2703023E1A24890210100000005"));
    }

    @Test
    public void testToGrpcObject() throws Exception {
        Transaction transaction = ImmutableTransaction.builder()
                .expirationTime(10)
                .maxGasAmount(6000)
                .gasUnitPrice(1)
                .sequenceNumber(1)
                .expirationTime(1L)
                .senderAccount(new byte[] { 1 })
                .program(ImmutableProgram.builder()
                        .addArguments(new U64Argument(1000), new AccountAddressArgument(new byte[] { 1 }))
                        .code(ByteString.copyFrom(new byte[] { 1 }))
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(KeyUtils.publicKeyFromHexString(PUBLIC_KEY_HEX))
                .signature(new byte[] { 1 })
                .transaction(transaction)
                .build();

        SubmitTransactionRequest request = signedTransaction.toGrpcObject();

        assertThat(Hex.toHexString(request.getTransaction().getTxnBytes().toByteArray()), is(
                "0101000000000000000000000001000000010200000000000000e803000000000000010000000100000000701700000000000001000000000000000100000000000000200000000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a24890210100000001"));
    }

}
