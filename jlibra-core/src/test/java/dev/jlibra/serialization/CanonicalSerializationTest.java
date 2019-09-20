package dev.jlibra.serialization;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.google.protobuf.ByteString;

import dev.jlibra.admissioncontrol.transaction.AccountAddressArgument;
import dev.jlibra.admissioncontrol.transaction.ImmutableProgram;
import dev.jlibra.admissioncontrol.transaction.ImmutableTransaction;
import dev.jlibra.admissioncontrol.transaction.Program;
import dev.jlibra.admissioncontrol.transaction.StringArgument;
import dev.jlibra.admissioncontrol.transaction.Transaction;
import dev.jlibra.admissioncontrol.transaction.TransactionArgument;
import dev.jlibra.admissioncontrol.transaction.U64Argument;

public class CanonicalSerializationTest {

    public void testSerializeInt() {
        assertThat(CanonicalSerialization.serializeInt(32), is("20000000"));
        assertThat(CanonicalSerialization.serializeInt(305419896), is("78563412"));
    }

    public void testSerializeLong() {
        assertThat(CanonicalSerialization.serializeLong(1311768467750121216L), is("00EFCDAB78563412"));
    }

    public void testSerializeString() {
        assertThat(CanonicalSerialization.serializeString("ሰማይ አይታረስ ንጉሥ አይከሰስ።"), is(
                "36000000E188B0E1889BE18BAD20E18AA0E18BADE189B3E188A8E188B520E18A95E18C89E188A520E18AA0E18BADE18AA8E188B0E188B5E18DA2"));
    }

    public void testSerializeByteArray() {
        byte[] byteArray = Hex.decode("ca820bf9305eb97d0d784f71b3955457fbf6911f5300ceaa5d7e8621529eae19");

        assertThat(CanonicalSerialization.serializeByteArray(byteArray),
                is("20000000CA820BF9305EB97D0D784F71B3955457FBF6911F5300CEAA5D7E8621529EAE19"));
    }

    @Test
    public void testSerializeU64Argument() {
        U64Argument arg = new U64Argument(9213671392124193148L);
        assertThat(Hex.toHexString(arg.serialize()).toUpperCase(),
                is("000000007CC9BDA45089DD7F"));
    }

    @Test
    public void testSerializeStringArgument() {
        StringArgument arg = new StringArgument("Hello, World!");
        assertThat(Hex.toHexString(arg.serialize()).toUpperCase(),
                is("020000000D00000048656C6C6F2C20576F726C6421"));
    }

    @Test
    public void testSerializeAccountAddressArgument() {
        AccountAddressArgument arg = new AccountAddressArgument(
                Hex.decode("2c25991785343b23ae073a50e5fd809a2cd867526b3c1db2b0bf5d1924c693ed"));
        assertThat(Hex.toHexString(arg.serialize()).toUpperCase(),
                is("01000000200000002C25991785343B23AE073A50E5FD809A2CD867526B3C1DB2B0BF5D1924C693ED"));
    }

    @Test
    public void testSerializeTransactionArguments() {
        List<TransactionArgument> arguments = Arrays.asList(new U64Argument(9213671392124193148L));

        System.out.println(Hex.toHexString(CanonicalSerialization.serializeTransactionArguments(arguments)));
    }

    @Test
    public void testSerializeProgram() {
        Program program = ImmutableProgram.builder()
                .addArguments(new StringArgument("CAFE D00D"), new StringArgument("cafe d00d"))
                .code(ByteString.copyFrom("move".getBytes(Charset.forName("UTF-8"))))
                .build();

        System.out.println("Prog " + Hex.toHexString(program.serialize()).toUpperCase());
    }

    @Test
    public void testSerializeTransaction() {
        Program program = ImmutableProgram.builder()
                .addArguments(new StringArgument("CAFE D00D"), new StringArgument("cafe d00d"))
                .code(ByteString.copyFrom("move".getBytes(Charset.forName("UTF-8"))))
                .build();

        Transaction transaction = ImmutableTransaction.builder()
                .sequenceNumber(32)
                .program(program)
                .maxGasAmount(10000)
                .gasUnitPrice(20000)
                .expirationTime(86400)
                .senderAccount(Hex.decode("3a24a61e05d129cace9e0efc8bc9e33831fec9a9be66f50fd352a2638a49b9ee"))
                .build();
        System.out.println("Trans " + Hex.toHexString(transaction.serialize()).toUpperCase());
    }
}
