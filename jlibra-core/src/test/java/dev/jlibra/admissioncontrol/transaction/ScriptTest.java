package dev.jlibra.admissioncontrol.transaction;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import dev.jlibra.serialization.ByteSequence;

public class ScriptTest {

    @Test
    public void testSerialize() {
        Script program = ImmutableScript.builder()
                .code(ByteSequence.from("move".getBytes()))
                .addArguments(new ByteArrayArgument(ByteSequence.from("CAFE D00D".getBytes(UTF_8))),
                        new ByteArrayArgument(ByteSequence.from("cafe d00d".getBytes(UTF_8))))
                .build();

        assertThat(program.serialize().toString().toUpperCase(), is(
                "02000000040000006D6F76650200000002000000090000004341464520443030440200000009000000636166652064303064"));
    }
}
