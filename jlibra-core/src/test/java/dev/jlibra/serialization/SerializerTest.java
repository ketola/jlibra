package dev.jlibra.serialization;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

public class SerializerTest {

    @Test
    public void testSerializeByte() {
        assertThat(Serializer.builder().appendByte((byte) 1).toByteArray().toString(), is("01"));
    }

    @Test
    public void testSerializeShort() {
        assertThat(Serializer.builder().appendShort((short) 4660).toByteArray().toString(), is("3412"));
    }

    @Test
    public void testSerializeInt() {
        assertThat(Serializer.builder().appendInt(32).toByteArray().toString(), is("20000000"));
        assertThat(Serializer.builder().appendInt(305419896).toByteArray().toString(), is("78563412"));
        assertThat(Serializer.builder().appendInt(32).appendInt(305419896).toByteArray().toString(),
                is("2000000078563412"));
    }

    @Test
    public void testSerializeLong() {
        assertThat(Serializer.builder().appendLong(1311768467750121216L).toByteArray().toString().toUpperCase(),
                is("00EFCDAB78563412"));
    }

    @Test
    public void testSerializeULEB128() {
        assertThat(Serializer.builder().appendIntAsLeb128(0).toByteArray().toString(), is("00"));
        assertThat(Serializer.builder().appendIntAsLeb128(10).toByteArray().toString(), is("0a"));
        assertThat(Serializer.builder().appendIntAsLeb128(100).toByteArray().toString(), is("64"));
        assertThat(Serializer.builder().appendIntAsLeb128(1000).toByteArray().toString(), is("e807"));
    }

    @Test
    public void testSerializeString() {
        assertThat(
                Serializer.builder().appendString("ሰማይ አይታረስ ንጉሥ አይከሰስ።").toByteArray().toString().toUpperCase(),
                is("36E188B0E1889BE18BAD20E18AA0E18BADE189B3E188A8E188B520E18A95E18C89E188A520E18AA0E18BADE18AA8E188B0E188B5E18DA2"));
    }

    @Test
    public void testSerializeByteSequence() {
        ByteArray byteSequence = ByteArray
                .from("ca820bf9305eb97d0d784f71b3955457fbf6911f5300ceaa5d7e8621529eae19");

        assertThat(Serializer.builder().append(byteSequence).toByteArray().toString().toUpperCase(),
                is("20CA820BF9305EB97D0D784F71B3955457FBF6911F5300CEAA5D7E8621529EAE19"));
    }

    @Test
    public void testSerializeByteSequenceWithoutLengthInformation() {
        ByteArray byteSequence = ByteArray
                .from("ca820bf9305eb97d0d784f71b3955457fbf6911f5300ceaa5d7e8621529eae19");

        assertThat(
                Serializer.builder().appendFixedLength(byteSequence).toByteArray().toString().toUpperCase(),
                is("CA820BF9305EB97D0D784F71B3955457FBF6911F5300CEAA5D7E8621529EAE19"));
    }
}
