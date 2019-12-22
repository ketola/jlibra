package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.ByteSequence;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TransactionTest {

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
                        .addArguments(new U64Argument(1000), new AccountAddressArgument(ByteSequence.from(new byte[] { 2 })))
                        .code(ByteSequence.from(new byte[] { 3 }))
                        .build())
                .build();

        assertThat(transaction.serialize().toString().toUpperCase(), is(
                "0104000000000000000200000001000000030200000000000000E8030000000000000100000002020000000000000003000000000000000500000000000000"));
    }

}
