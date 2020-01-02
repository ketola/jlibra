package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ByteArrayArgumentTest {

    @Test
    public void testSerialize() {
        ByteArrayArgument arg = new ByteArrayArgument(ByteSequence.from("hello".getBytes()));
        byte[] serializedByteArrayArgument = arg.serialize().toArray();
        assertThat(Hex.toHexString(serializedByteArrayArgument), is("030000000500000068656c6c6f"));
    }
}
