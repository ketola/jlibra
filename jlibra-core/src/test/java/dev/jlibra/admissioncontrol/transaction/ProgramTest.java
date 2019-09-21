package dev.jlibra.admissioncontrol.transaction;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.google.protobuf.ByteString;

public class ProgramTest {

    @Test
    public void testSerialize() {
        Program program = ImmutableProgram.builder()
                .code(ByteString.copyFrom("move".getBytes()))
                .addArguments(new StringArgument("CAFE D00D"), new StringArgument("cafe d00d"))
                .build();

        assertThat(Hex.toHexString(program.serialize()).toUpperCase(), is(
                "00000000040000006D6F7665020000000200000009000000434146452044303044020000000900000063616665206430306400000000"));
    }
}
