package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import org.junit.BeforeClass;
import org.junit.Test;

import admission_control.AdmissionControlOuterClass.SubmitTransactionRequest;
import dev.jlibra.AccountAddress;
import dev.jlibra.KeyUtils;
import dev.jlibra.serialization.ByteSequence;

public class SignedTransactionTest {
    private static final ByteSequence PUBLIC_KEY_HEX = KeyUtils.stripPublicKeyPrefix(ByteSequence
            .from("302a300506032b65700321000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021"));
    private static final ByteSequence PRIVATE_KEY_HEX = ByteSequence
            .from("3051020101300506032b6570042204206dadf7a252c0e74add2e545a1e3c811f1f4bdd88f8c5e0080e068f4df6d909128121000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021");

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
                .senderAccount(AccountAddress.ofByteSequence(ByteSequence.from(new byte[] { 1 })))
                .payload(ImmutableScript.builder()
                        .addArguments(new U64Argument(1000),
                                new AccountAddressArgument(
                                        AccountAddress.ofByteSequence(ByteSequence.from(new byte[] { 2 }))))
                        .code(ByteSequence.from(new byte[] { 3 }))
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(PUBLIC_KEY_HEX)
                .signature(Signature.signTransaction(transaction,
                        KeyUtils.privateKeyFromByteSequence(PRIVATE_KEY_HEX)))
                .transaction(transaction)
                .build();

        // assertThat(signedTransaction.serialize().getValue().toString().toUpperCase(),
        // is(
        // "0104000000000000000200000001000000030200000000000000E8030000000000000100000002020000000000000003000000000000000500000000000000200000000B29A7ADCE0897B2D1EC18CC482237463EFA173945FA3BD2703023E1A24890214000000039856908D3C9ACCFA01E9403583A48C01B93C71600067D3422C7A3612EC213FF18355795E3E702ECD709F2361126CD14E573046C7FC3AEC34AB3EA98BE695A09"));
    }

    @Test
    public void testToGrpcObject() {
        Transaction transaction = ImmutableTransaction.builder()
                .expirationTime(10)
                .maxGasAmount(6000)
                .gasUnitPrice(1)
                .sequenceNumber(1)
                .expirationTime(1L)
                .senderAccount(
                        AccountAddress.ofByteSequence(ByteSequence.from(new byte[] { 1 })))
                .payload(ImmutableScript.builder()
                        .addArguments(new U64Argument(1000),
                                new AccountAddressArgument(
                                        AccountAddress.ofByteSequence(ByteSequence.from(new byte[] { 1 }))))
                        .code(ByteSequence.from(new byte[] { 1 }))
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .publicKey(PUBLIC_KEY_HEX)
                .signature(Signature.signTransaction(transaction, KeyUtils.privateKeyFromByteSequence(PRIVATE_KEY_HEX)))
                .transaction(transaction)
                .build();

        SubmitTransactionRequest request = signedTransaction.toGrpcObject();

        assertThat(Hex.toHexString(request.getTransaction().getTxnBytes().toByteArray()), is(
                "0101000000000000000200000001000000010200000000000000e8030000000000000100000001701700000000000001000000000000000100000000000000200000000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021400000003bd4006c7af91bd529000c0c726b901a7afe0659206618a6f6d13784a1674bafc8a3bcebc888c52724af66d457c7135a478d176682f2424e9f01ff92e341480d"));
    }

}
