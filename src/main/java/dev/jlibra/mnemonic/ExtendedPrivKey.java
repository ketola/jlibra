package dev.jlibra.mnemonic;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import types.Transaction.RawTransaction;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ExtendedPrivKey {

    public final SecretKey privateKey;
    public final SecretKey publicKey;

    public ExtendedPrivKey(SecretKey secretKey) {
        this.privateKey = secretKey;
        this.publicKey = new SecretKey(createPublic(secretKey));
    }

    private static byte[] createPublic(SecretKey secretKey) {
        // TODO

        return new byte[0];
    }

    public String getAddress() {
        SHA3.Digest256 digest256 = new SHA3.Digest256();
        digest256.update(publicKey.getData());
        return Hex.toHexString(digest256.digest());
    }

    public byte[] sign(RawTransaction rawTransaction) {
        // TODO

        return new byte[0];
    }
}
