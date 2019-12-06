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
import dev.jlibra.AccountAddress;
import dev.jlibra.KeyUtils;

public class SignedTransactionTest {
    private static final String PUBLIC_KEY_HEX = "302a300506032b65700321000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021";
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

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(KeyUtils.publicKeyFromHexString(PUBLIC_KEY_HEX))
                .privateKey(KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX))
                .transaction(transaction)
                .build();

        assertThat(Hex.toHexString(signedTransaction.serialize()).toUpperCase(), is(
                "0104000000000000000000000001000000030200000000000000E803000000000000010000000200000000020000000000000003000000000000000500000000000000200000000B29A7ADCE0897B2D1EC18CC482237463EFA173945FA3BD2703023E1A248902140000000CDB05E40081406F4813B5A3122B3C09A5C6C5023C33B920D6D840B568B905281857D6E95582017423F6FD9721A2E40CFDCCD6BA23EAF6426B87F4C62B4EE4B03"));
    }

    @Test
    public void testSignature() {
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

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(KeyUtils.publicKeyFromHexString(PUBLIC_KEY_HEX))
                .privateKey(KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX))
                .transaction(transaction)
                .build();

        assertThat(Hex.toHexString(
                signedTransaction.signTransaction(transaction, KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX))),
                is("cdb05e40081406f4813b5a3122b3c09a5c6c5023c33b920d6d840b568b905281857d6e95582017423f6fd9721a2e40cfdccd6ba23eaf6426b87f4c62b4ee4b03"));
    }

    @Test
    public void testToGrpcObject() throws Exception {
        Transaction transaction = ImmutableTransaction.builder()
                .expirationTime(10)
                .maxGasAmount(6000)
                .gasUnitPrice(1)
                .sequenceNumber(1)
                .expirationTime(1L)
                .senderAccount(AccountAddress.ofByteArray(new byte[] { 1 }))
                .program(ImmutableProgram.builder()
                        .addArguments(new U64Argument(1000), new AccountAddressArgument(new byte[] { 1 }))
                        .code(ByteString.copyFrom(new byte[] { 1 }))
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(KeyUtils.publicKeyFromHexString(PUBLIC_KEY_HEX))
                .privateKey(KeyUtils.privateKeyFromHexString(PRIVATE_KEY_HEX))
                .transaction(transaction)
                .build();

        SubmitTransactionRequest request = signedTransaction.toGrpcObject();

        assertThat(Hex.toHexString(request.getTransaction().getTxnBytes().toByteArray()), is(
                "0101000000000000000000000001000000010200000000000000e803000000000000010000000100000000701700000000000001000000000000000100000000000000200000000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a248902140000000a6031857e3c1518bc6790b7554af36a873cb23a1432dc78dfc26670a4d9502e36623cf14eff09925f26644a811fd93d17549b1d3d05cc38f3dd4e7e2a02d5608"));
    }

}
