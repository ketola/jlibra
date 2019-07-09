package dev.jlibra.mnemonic;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ExtendedPrivKey {
    public final ChildNumber childNumber;
    public final SecretKey privateKey;

    public ExtendedPrivKey(ChildNumber childNumber, SecretKey privateKey) {
        this.childNumber = childNumber;
        this.privateKey = privateKey;
    }
}
