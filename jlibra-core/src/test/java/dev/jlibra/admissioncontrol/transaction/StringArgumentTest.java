package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

public class StringArgumentTest {

    @Test
    public void testSerialize() {
        StringArgument argument = new StringArgument("Hello, World!");

        assertThat(Hex.toHexString(argument.serialize()).toUpperCase(),
                is("020000000D00000048656C6C6F2C20576F726C6421"));
    }
}
