package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class U64ArgumentTest {

    @Test
    public void testSerialize() {
        U64Argument argument = new U64Argument(9213671392124193148L);

        assertThat(Hex.toHexString(argument.serialize()).toUpperCase(),
                is("000000007CC9BDA45089DD7F"));
    }
}
