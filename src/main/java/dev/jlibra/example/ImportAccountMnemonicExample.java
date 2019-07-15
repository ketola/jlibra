package dev.jlibra.example;

import dev.jlibra.mnemonic.*;

import java.security.Security;

public class ImportAccountMnemonicExample {

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Seed seed = new Seed(Mnemonic.fromString("legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will"), "LIBRA");
        LibraKeyFactory libraKeyFactory = new LibraKeyFactory(seed);
        ExtendedPrivKey extendedPrivKey = libraKeyFactory.privateChild(new ChildNumber(0));

        System.out.println("Libra address: " + extendedPrivKey.getAddress());
        System.out.println("Public key: " + extendedPrivKey.publicKey);
        System.out.println("Private key: " + extendedPrivKey.privateKey);
    }
}
