package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.security.PrivateKey;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import dev.jlibra.AccountAddress;
import dev.jlibra.KeyUtils;
import dev.jlibra.serialization.ByteSequence;

public class SignatureTest {

    private static final ByteSequence PRIVATE_KEY_BYTES = ByteSequence
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

        PrivateKey privateKey = KeyUtils.privateKeyFromByteSequence(PRIVATE_KEY_BYTES);
        Signature signature = ImmutableSignature.builder()
                .privateKey(privateKey)
                .transaction(transaction)
                .build();

        assertThat(signature.signTransaction(transaction, privateKey).toString(), is(
                "39856908d3c9accfa01e9403583a48c01b93c71600067d3422c7a3612ec213ff18355795e3e702ecd709f2361126cd14e573046c7fc3aec34ab3ea98be695a09"));
        assertThat(signature.serialize().toString(), is(
                "4000000039856908d3c9accfa01e9403583a48c01b93c71600067d3422c7a3612ec213ff18355795e3e702ecd709f2361126cd14e573046c7fc3aec34ab3ea98be695a09"));
    }

}
