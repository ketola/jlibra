package dev.jlibra.serialization.lcs;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;

import dev.jlibra.AccountAddress;
import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class LCSSerializerTest {

    @Test
    public void serializeTransaction() {
        Transaction transaction = ImmutableTransaction.builder()
                .expirationTime(1L)
                .gasUnitPrice(1)
                .maxGasAmount(2)
                .payload(ImmutableScript.builder()
                        .code(
                                ByteArray.from("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"))
                        .arguments(
                                asList(new AccountAddressArgument(AccountAddress.fromByteArray(ByteArray.from(
                                        "8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d")))))
                        .build())
                .senderAccount(AccountAddress.fromByteArray(
                        ByteArray
                                .from("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d")))
                .sequenceNumber(3)
                .build();

        LCSSerializer ser = new LCSSerializer();
        ByteSequence bytes = ser.serialize(transaction, Transaction.class);

        assertThat(bytes.toString(), Matchers.is(
                "8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d030000000000000002000000200000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d01000000010000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d020000000000000001000000000000000100000000000000"));

    }

}
