package dev.jlibra.serialization;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SerializerTest {

    @Test
    public void testSerializeInt() {
        assertThat(Serializer.builder().appendInt(32).toByteSequence().toString(), is("20000000"));
        assertThat(Serializer.builder().appendInt(305419896).toByteSequence().toString(), is("78563412"));
        assertThat(Serializer.builder().appendInt(32).appendInt(305419896).toByteSequence().toString(),
                is("2000000078563412"));
    }

    @Test
    public void testSerializeLong() {
        assertThat(Serializer.builder().appendLong(1311768467750121216L).toByteSequence().toString().toUpperCase(),
                is("00EFCDAB78563412"));
    }

    @Test
    public void testSerializeString() {
        assertThat(
                Serializer.builder().appendString("ሰማይ አይታረስ ንጉሥ አይከሰስ።").toByteSequence().toString().toUpperCase(),
                is("36000000E188B0E1889BE18BAD20E18AA0E18BADE189B3E188A8E188B520E18A95E18C89E188A520E18AA0E18BADE18AA8E188B0E188B5E18DA2"));
    }

    @Test
    public void testSerializeByteSequence() {
        ByteSequence byteSequence = ByteSequence
                .from("ca820bf9305eb97d0d784f71b3955457fbf6911f5300ceaa5d7e8621529eae19");

        assertThat(Serializer.builder().append(byteSequence).toByteSequence().toString().toUpperCase(),
                is("20000000CA820BF9305EB97D0D784F71B3955457FBF6911F5300CEAA5D7E8621529EAE19"));
    }

    @Test
    public void testSerializeByteSequenceWithoutLengthInformation() {
        ByteSequence byteSequence = ByteSequence
                .from("ca820bf9305eb97d0d784f71b3955457fbf6911f5300ceaa5d7e8621529eae19");

        assertThat(
                Serializer.builder().appendFixedLength(byteSequence).toByteSequence().toString().toUpperCase(),
                is("CA820BF9305EB97D0D784F71B3955457FBF6911F5300CEAA5D7E8621529EAE19"));
    }
}
