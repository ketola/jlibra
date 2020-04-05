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
import dev.jlibra.serialization.ByteArray;

public class SignedTransactionTest {
    private static final ByteArray PUBLIC_KEY_HEX = KeyUtils.stripPublicKeyPrefix(ByteArray
            .from("302a300506032b65700321000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021"));
    private static final ByteArray PRIVATE_KEY_HEX = ByteArray
            .from("3051020101300506032b6570042204206dadf7a252c0e74add2e545a1e3c811f1f4bdd88f8c5e0080e068f4df6d909128121000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021");

    @BeforeClass
    public static void setUpClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void testToGrpcObject() {
        Transaction transaction = ImmutableTransaction.builder()
                .expirationTime(10)
                .maxGasAmount(6000)
                .gasUnitPrice(1)
                .sequenceNumber(1)
                .expirationTime(1L)
                .gasSpecifier(LbrTypeTag.build())
                .senderAccount(
                        AccountAddress.fromByteArray(ByteArray.from(new byte[] { 1 })))
                .payload(ImmutableScript.builder()
                        .addArguments(new U64Argument(1000),
                                ImmutableAccountAddressArgument.builder()
                                        .value(AccountAddress.fromByteArray(ByteArray.from(new byte[] { 1 })))
                                        .build())
                        .code(ByteArray.from(new byte[] { 1 }))
                        .build())
                .build();

        SignedTransaction signedTransaction = ImmutableSignedTransaction.builder()
                .authenticator(ImmutableTransactionAuthenticator.builder()
                        .publicKey(PUBLIC_KEY_HEX)
                        .signature(Signature.signTransaction(transaction,
                                KeyUtils.privateKeyFromByteSequence(PRIVATE_KEY_HEX)))
                        .build())
                .transaction(transaction)
                .build();

        SubmitTransactionRequest request = signedTransaction.toGrpcObject();

        assertThat(Hex.toHexString(request.getTransaction().getTxnBytes().toByteArray()), is(
                "0101000000000000000201010200e8030000000000000101701700000000000001000000000000000600000000000000000000000000000000034c4252015400010000000000000000200b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a248902140bf580efe3b02cb02511abbd909396ce5308c169ac34bfbae206082d3d1ced5c28f7b1bfffbec4692b93e6d860aef69d43e3bec50f8fed13b72ca840a0f125e06"));
    }

}
