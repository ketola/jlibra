package dev.jlibra;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class Hash {

    private ByteArray input;

    private Hash(ByteArray input) {
        this.input = input;
    }

    public static Hash ofInput(ByteArray input) {
        return new Hash(input);
    }

    public ByteArray hash() {
        return ByteArray.from(new SHA3.Digest256().digest(input.toArray()));
    }

    public ByteArray hash(ByteSequence salt) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] saltDigest = digestSHA3.digest(salt.toArray());
        byte[] saltDigestAndInput = new byte[saltDigest.length + input.toArray().length];
        System.arraycopy(saltDigest, 0, saltDigestAndInput, 0, saltDigest.length);
        System.arraycopy(input.toArray(), 0, saltDigestAndInput, saltDigest.length, input.toArray().length);
        return ByteArray.from(saltDigestAndInput);
    }

}
