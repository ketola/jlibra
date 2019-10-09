package dev.jlibra.mnemonic;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ChildNumber {

    public final long data;

    public ChildNumber(long data) {
        this.data = data;
    }

    public ChildNumber increment() {
        return new ChildNumber(data + 1);
    }

}
