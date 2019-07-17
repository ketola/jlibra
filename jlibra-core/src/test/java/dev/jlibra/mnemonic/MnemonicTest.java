package dev.jlibra.mnemonic;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MnemonicTest {

    @Test
    public void fromSeed() {
        // test data from https://github.com/libra/libra/blob/master/client/libra_wallet/src/key_factory.rs
        byte[] data = Hex.decode("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f");
        Mnemonic mnemonic = Mnemonic.fromString("legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will");
        assertEquals(
                mnemonic.toString(),
                Mnemonic.fromBytes(data).toString()
        );
    }
}
