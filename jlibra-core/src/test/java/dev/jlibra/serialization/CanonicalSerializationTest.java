package dev.jlibra.serialization;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import dev.jlibra.admissioncontrol.transaction.TransactionArgument;
import dev.jlibra.admissioncontrol.transaction.U64Argument;

public class CanonicalSerializationTest {

    @Test
    public void testSerializeInt() {
        assertThat(Hex.toHexString(CanonicalSerialization.serializeInt(32)), is("20000000"));
        assertThat(Hex.toHexString(CanonicalSerialization.serializeInt(305419896)), is("78563412"));
    }

    @Test
    public void testSerializeLong() {
        assertThat(Hex.toHexString(CanonicalSerialization.serializeLong(1311768467750121216L)).toUpperCase(),
                is("00EFCDAB78563412"));
    }

    @Test
    public void testSerializeString() {
        assertThat(Hex.toHexString(CanonicalSerialization.serializeString("ሰማይ አይታረስ ንጉሥ አይከሰስ።")).toUpperCase(), is(
                "36000000E188B0E1889BE18BAD20E18AA0E18BADE189B3E188A8E188B520E18A95E18C89E188A520E18AA0E18BADE18AA8E188B0E188B5E18DA2"));
    }

    @Test
    public void testSerializeByteArray() {
        byte[] byteArray = Hex.decode("ca820bf9305eb97d0d784f71b3955457fbf6911f5300ceaa5d7e8621529eae19");

        assertThat(Hex.toHexString(CanonicalSerialization.serializeByteArray(byteArray)).toUpperCase(),
                is("20000000CA820BF9305EB97D0D784F71B3955457FBF6911F5300CEAA5D7E8621529EAE19"));
    }

    @Test
    public void testSerializeTransactionArguments() {
        List<TransactionArgument> arguments = Arrays.asList(new U64Argument(9213671392124193148L));

        assertThat(Hex.toHexString(CanonicalSerialization.serializeTransactionArguments(arguments)).toUpperCase(),
                is("01000000000000007CC9BDA45089DD7F"));
    }
}
