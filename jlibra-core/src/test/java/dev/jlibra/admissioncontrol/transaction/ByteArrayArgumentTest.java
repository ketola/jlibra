package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import dev.jlibra.serialization.ByteSequence;

public class ByteArrayArgumentTest {

    @Test
    public void testSerialize() {
        ByteArrayArgument arg = new ByteArrayArgument(ByteSequence.from("hello".getBytes()));
        byte[] serializedByteArrayArgument = arg.serialize().toArray();
        assertThat(Hex.toHexString(serializedByteArrayArgument), is("020000000500000068656c6c6f"));
    }
}
