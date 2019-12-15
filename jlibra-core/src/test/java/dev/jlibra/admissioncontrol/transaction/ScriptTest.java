package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.google.protobuf.ByteString;

public class ScriptTest {

    @Test
    public void testSerialize() {
        Script program = ImmutableScript.builder()
                .code(ByteString.copyFrom("move".getBytes()))
                .addArguments(new StringArgument("CAFE D00D"), new StringArgument("cafe d00d"))
                .build();

        assertThat(Hex.toHexString(program.serialize()).toUpperCase(), is(
                "02000000040000006D6F76650200000002000000090000004341464520443030440200000009000000636166652064303064"));
    }
}
