package dev.jlibra;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import dev.jlibra.serialization.ByteSequence;

public class KeyUtils {
    public static PrivateKey privateKeyFromByteSequence(ByteSequence privateKey) {
        try {
            return getKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(privateKey.toArray()));
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
