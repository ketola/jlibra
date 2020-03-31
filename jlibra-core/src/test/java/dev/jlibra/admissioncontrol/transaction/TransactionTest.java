package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.google.protobuf.ByteString;

public class TransactionTest {

    @Test
    public void testFromGrpcObject() {
        String transactionBytes = "0101000000000000000200000001000000010200000000000000e8030000000000000100000001701700000000000001000000000000000600000000000000000000000000000000000000030000004c4252010000005400000000010000000000000000000000200000000b29a7adce0897b2d1ec18cc482237463efa173945fa3bd2703023e1a2489021400000002dd9560ac445cf744080d3d53d76533ac317b427085b07992ca8211c9655a64290f1577d3f599a247b3ef82a5a826864683343eda1054e981060661ce4aac70f";
        types.TransactionOuterClass.Transaction t = types.TransactionOuterClass.Transaction.newBuilder()
                .setTransaction(ByteString.copyFrom(Hex.decode(transactionBytes)))
                .build();

        Transaction transaction = Transaction.fromGrpcObject(t);

        assertThat(transaction.getSenderAccount().toString(),
                is("d5586b1c04555911fb3c0ab6f60261ad242b3eb4d0eddd2ba22c02174d6173c4"));
        assertThat(transaction.getSequenceNumber(), is(3L));
        assertThat(transaction.getPayload().getCode().toArray().length, is(184));
        assertThat(transaction.getPayload().getArguments().size(), is(2));
        assertThat(
                ((AccountAddressArgument) transaction.getPayload().getArguments().get(0)).getValue()
                        .toString(),
                is("8b8dda4052b55bb475f5e69a160013508ca20e3766fb33b6a7e0325611fdeb22"));
        assertThat(((U64Argument) transaction.getPayload().getArguments().get(1)).getValue(), is(1_000_000L));
        assertThat(transaction.getExpirationTime(), is(1577527101L));
        assertThat(transaction.getGasUnitPrice(), is(0L));
        assertThat(transaction.getMaxGasAmount(), is(140_000L));

    }

}
