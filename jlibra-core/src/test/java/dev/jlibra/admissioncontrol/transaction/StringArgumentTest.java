package dev.jlibra.admissioncontrol.transaction;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringArgumentTest {

    @Test
    public void testSerialize() {
        StringArgument argument = new StringArgument("Hello, World!");

        assertThat(argument.serialize().toString().toUpperCase(),
                is("020000000D00000048656C6C6F2C20576F726C6421"));
    }
}
