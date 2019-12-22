package dev.jlibra.mnemonic;

import dev.jlibra.serialization.ByteSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MnemonicTest {

    @Test
    public void fromSeed() {
        ByteSequence byteSequence = ByteSequence.from("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f");
        Mnemonic mnemonic = Mnemonic.fromString("legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will");
        assertEquals(
                mnemonic.toString(),
                Mnemonic.fromByteSequence(byteSequence).toString()
        );
    }
}
