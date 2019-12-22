package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.ByteSequence;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ScriptTest {

    @Test
    public void testSerialize() {
        Script program = ImmutableScript.builder()
                .code(ByteSequence.from("move".getBytes()))
                .addArguments(new StringArgument("CAFE D00D"), new StringArgument("cafe d00d"))
                .build();

        assertThat(program.serialize().toString().toUpperCase(), is(
                "02000000040000006D6F76650200000002000000090000004341464520443030440200000009000000636166652064303064"));
    }
}
