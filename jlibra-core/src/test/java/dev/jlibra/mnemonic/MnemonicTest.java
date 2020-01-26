package dev.jlibra.mnemonic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dev.jlibra.serialization.ByteArray;

public class MnemonicTest {

    @Test
    public void fromSeed() {
        ByteArray byteSequence = ByteArray.from("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f");
        Mnemonic mnemonic = Mnemonic.fromString(
                "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will");
        assertEquals(
                mnemonic.toString(),
                Mnemonic.fromByteSequence(byteSequence).toString());
    }
}
