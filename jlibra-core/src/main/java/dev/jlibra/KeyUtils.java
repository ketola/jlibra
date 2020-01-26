package dev.jlibra;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class KeyUtils {

    public static ByteArray stripPublicKeyPrefix(ByteArray pubKeyBytes) {
        return pubKeyBytes.subseq(12, 32);
    }

    public static PrivateKey privateKeyFromByteSequence(ByteSequence privateKey) {
        try {
            return getKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(privateKey.toArray()));
        } catch (InvalidKeySpecException e) {
            throw new LibraRuntimeException("PrivateKey generation failed", e);
        }
    }

    public static PublicKey publicKeyFromByteSequence(ByteSequence publicKey) {
        try {
            return getKeyFactory().generatePublic(new X509EncodedKeySpec(publicKey.toArray()));
        } catch (InvalidKeySpecException e) {
            throw new LibraRuntimeException("PrivateKey generation failed", e);
        }
    }

    public static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            throw new LibraRuntimeException("Could not get KeyFactory", e);
        }
    }

}
