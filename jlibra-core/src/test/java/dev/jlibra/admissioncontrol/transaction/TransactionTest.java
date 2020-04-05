package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.google.protobuf.ByteString;

public class TransactionTest {

    @Test
    public void testFromGrpcObject() {
        String transactionBytes = "000000004e03aec69589026b4a095c9cd2e53ca6000000000000000002000000cd000000a11ceb0b010008014f000000060000000255000000040000000359000000060000000c5f000000110000000d700000000b000000057b0000002f00000004aa0000001000000007ba0000001300000000000001000201030200020400000501020003050a02030101020003050a0203000303050a02030301080000063c53454c463e034c42520c4c696272614163636f756e7401540f7061795f66726f6d5f73656e646572046d61696e00000000000000000000000000000000010000ffff030005000a000b010a021200010203000000010000000990455b46e5eee5145e4de4be27ce73020000001000000070bd2e16ef97a72d681151ddc9ffa3bf0000000040420f0000000000203005000000000000000000000000000600000000000000000000000000000000000000030000004c4252010000005400000000645a835e00000000000000002000000081baa2b679aa2c8fb75dbe2f9164eef0265be7bb6c20c81c95a788997d927e3f4000000042c4d66b5f2deecbf380c6dca863ef8200f40b8a91e3fb0b4f1fdde91ec833bd78308ef91dd23035614873ca981694da1646671ddd7cfcad1018da948058450d";
        types.TransactionOuterClass.Transaction t = types.TransactionOuterClass.Transaction.newBuilder()
                .setTransaction(ByteString.copyFrom(Hex.decode(transactionBytes)))
                .build();

        Transaction transaction = Transaction.fromGrpcObject(t);

        assertThat(transaction.senderAccount().toString(),
                is("4e03aec69589026b4a095c9cd2e53ca6"));
        assertThat(transaction.sequenceNumber(), is(0L));
        assertThat(((Script) transaction.payload()).code().toArray().length, is(205));
        assertThat(((Script) transaction.payload()).arguments().size(), is(3));
        assertThat(
                ((AccountAddressArgument) ((Script) transaction.payload()).arguments().get(0)).value()
                        .toString(),
                is("0990455b46e5eee5145e4de4be27ce73"));
        assertThat(((U64Argument) ((Script) transaction.payload()).arguments().get(2)).getValue(), is(1_000_000L));
        assertThat(transaction.expirationTime(), is(1585666660L));
        assertThat(transaction.gasUnitPrice(), is(0L));
        assertThat(transaction.maxGasAmount(), is(340_000L));

    }

}
