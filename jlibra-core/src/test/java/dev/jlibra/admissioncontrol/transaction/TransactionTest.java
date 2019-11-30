package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.google.protobuf.ByteString;

import dev.jlibra.AccountAddress;

public class TransactionTest {

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

        assertThat(Hex.toHexString(transaction.serialize()).toUpperCase(), is(
                "0104000000000000000000000001000000030200000000000000E803000000000000010000000200000000020000000000000003000000000000000500000000000000"));
    }

}
