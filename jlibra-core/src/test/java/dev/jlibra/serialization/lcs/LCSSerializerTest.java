package dev.jlibra.serialization.lcs;

import static java.util.Arrays.asList;

import java.util.Date;

import org.junit.Test;

import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableFixedLengthByteSequence;
import dev.jlibra.admissioncontrol.transaction.ImmutableScript;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.VariableLengthByteSequence;
import dev.jlibra.serialization.ByteSequence;

public class LCSSerializerTest {

    @Test
    public void serializeTransaction() {
        Transaction transaction = ImmutableTransaction.builder()
                .expirationTime(new Date().getTime())
                .gasUnitPrice(1)
                .maxGasAmount(2)
                .payload(ImmutableScript.builder()
                        .code(VariableLengthByteSequence.ofByteSequence(
                                ByteSequence.from("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d")))
                        .arguments(
                                asList(new AccountAddressArgument(ImmutableFixedLengthByteSequence.builder()
                                        .value(ByteSequence.from(
                                                "8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"))
                                        .build())))
                        .build())
                .senderAccount(
                        ImmutableFixedLengthByteSequence.builder()
                                .value(ByteSequence
                                        .from("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"))
                                .build())
                .sequenceNumber(3)
                .build();

        LCSSerializer ser = new LCSSerializer();
        VariableLengthByteSequence bytes = ser.serialize(transaction, Transaction.class);
        // System.out.println(Hex.toHexString(bytes.toArray()));
        // System.out.println(Hex.toHexString(transaction.serialize().toArray()));
    }

}
