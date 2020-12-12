package dev.jlibra.example;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.mnemonic.ChildNumber;
import dev.jlibra.mnemonic.ExtendedPrivKey;
import dev.jlibra.mnemonic.LibraKeyFactory;
import dev.jlibra.mnemonic.Mnemonic;
import dev.jlibra.mnemonic.Seed;

public class ImportAccountMnemonicExample {

    private static final Logger logger = LoggerFactory.getLogger(ImportAccountMnemonicExample.class);

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        Seed seed = new Seed(Mnemonic.fromString(
                "legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will"),
                "LIBRA");
        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);
        ExtendedPrivKey extendedPrivKey = libraKeyFactory.privateChild(new ChildNumber(0));

        logger.info("Diem address: {}", extendedPrivKey.getAddress());
        logger.info("Public key: {}", extendedPrivKey.publicKey);
        logger.info("Private key: {}", extendedPrivKey.privateKey);
    }
}
