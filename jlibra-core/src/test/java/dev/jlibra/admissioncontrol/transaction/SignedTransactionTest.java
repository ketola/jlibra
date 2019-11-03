package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.google.protobuf.ByteString;

public class SignedTransactionTest {

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
                .publicKey(new byte[] { 4 })
                .signature(new byte[] { 5 })
                .transaction(transaction)
                .build();

        assertThat(Hex.toHexString(signedTransaction.serialize()).toUpperCase(), is(
                "0104000000000000000000000001000000030200000000000000E80300000000000001000000020000000002000000000000000300000000000000050000000000000001000000040100000005"));
    }

}
