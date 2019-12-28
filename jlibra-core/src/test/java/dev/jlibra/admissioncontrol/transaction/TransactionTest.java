package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.google.protobuf.ByteString;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.ByteSequence;

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
                        .addArguments(new U64Argument(1000),
                                new AccountAddressArgument(ByteSequence.from(new byte[] { 2 })))
                        .code(ByteSequence.from(new byte[] { 3 }))
                        .build())
                .build();

        assertThat(transaction.serialize().toString().toUpperCase(), is(
                "0104000000000000000200000001000000030200000000000000E8030000000000000100000002020000000000000003000000000000000500000000000000"));
    }

    @Test
    public void testFromGrpcObject() {
        String transactionBytes = "00000000d5586b1c04555911fb3c0ab6f60261ad242b3eb4d0eddd2ba22c02174d6173c4030000000000000002000000b80000004c49425241564d0a010007014a00000004000000034e000000060000000d54000000060000000e5a0000000600000005600000002900000004890000002000000008a90000000f00000000000001000200010300020002040200030204020300063c53454c463e0c4c696272614163636f756e74046d61696e0f7061795f66726f6d5f73656e6465720000000000000000000000000000000000000000000000000000000000000000000100020004000c000c011301010202000000010000008b8dda4052b55bb475f5e69a160013508ca20e3766fb33b6a7e0325611fdeb220000000040420f0000000000e02202000000000000000000000000003d27075e000000002000000075a8b972fe7e72e01f2f239363eb30481a82a476ad6d139fd6060d2ca5922bdc40000000aa7de9cd970bfa95392e2a11463a57e595c56692f53a0b8527797730844724c104263a570a275db633a06b43f61685a544bb88ff33c8b3b4681fe1ffe46c5a0a";
        types.TransactionOuterClass.Transaction t = types.TransactionOuterClass.Transaction.newBuilder()
                .setTransaction(ByteString.copyFrom(Hex.decode(transactionBytes)))
                .build();

        Transaction transaction = Transaction.fromGrpcObject(t);

        assertThat(transaction.getSenderAccount().getByteSequence().toString(),
                is("d5586b1c04555911fb3c0ab6f60261ad242b3eb4d0eddd2ba22c02174d6173c4"));
        assertThat(transaction.getSequenceNumber(), is(3L));
        assertThat(transaction.getPayload().getCode().toArray().length, is(184));
        assertThat(transaction.getPayload().getArguments().size(), is(2));
        assertThat(((AccountAddressArgument) transaction.getPayload().getArguments().get(0)).getValue().toString(),
                is("8b8dda4052b55bb475f5e69a160013508ca20e3766fb33b6a7e0325611fdeb22"));
        assertThat(((U64Argument) transaction.getPayload().getArguments().get(1)).getValue(), is(1_000_000L));
        assertThat(transaction.getExpirationTime(), is(1577527101L));
        assertThat(transaction.getGasUnitPrice(), is(0L));
        assertThat(transaction.getMaxGasAmount(), is(140_000L));

    }

}
