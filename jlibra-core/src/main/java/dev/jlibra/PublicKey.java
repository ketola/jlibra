package dev.jlibra;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class PublicKey implements ByteSequence {

    public static final int PUBLIC_KEY_LENGTH = 32;

    private ByteArray bytes;

    private PublicKey(ByteArray bytes) {
        this.bytes = bytes;
    }

    public static PublicKey fromPublicKey(java.security.PublicKey pk) {
        return new PublicKey(stripPublicKeyPrefix(ByteArray.from(pk.getEncoded())));
    }

    public static PublicKey fromByteSequence(ByteArray byteSequence) {
        return new PublicKey(byteSequence);
    }

    public static PublicKey fromHexString(String hexString) {
        try {
            return fromPublicKey(getKeyFactory().generatePublic(new X509EncodedKeySpec(Hex.decode(hexString))));
        } catch (InvalidKeySpecException e) {
            throw new LibraRuntimeException("PrivateKey generation failed", e);
        }
    }

    private static ByteArray stripPublicKeyPrefix(ByteArray pubKeyBytes) {
        return pubKeyBytes.subseq(12, 32);
    }

    private static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            throw new LibraRuntimeException("Could not get KeyFactory", e);
        }
    }

    @Override
    public byte[] toArray() {
        return bytes.toArray();
    }

    @Override
    public String toString() {
        return Hex.toHexString(bytes.toArray());
    }
}
