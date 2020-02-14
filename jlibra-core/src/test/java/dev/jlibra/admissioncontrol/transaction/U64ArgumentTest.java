package dev.jlibra.admissioncontrol.transaction;

import org.junit.Test;

public class U64ArgumentTest {

    @Test
    public void testSerialize() {
        U64Argument argument = new U64Argument(9213671392124193148L);

        // assertThat(argument.serialize().toString().toUpperCase(),
        // is("000000007CC9BDA45089DD7F"));
    }
}
