package dev.jlibra;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class KeyUtils {

    public static String toHexStringLibraAddress(byte[] publicKeyBytes) {
        return Hex.toHexString(toByteArrayLibraAddress(publicKeyBytes));
    }

    public static byte[] toByteArrayLibraAddress(byte[] publicKeyBytes) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        return digestSHA3.digest(stripPublicKeyPrefix(publicKeyBytes));
    }

    public static byte[] stripPublicKeyPrefix(byte[] pubKeyBytes) {
        byte[] publicKeyWithoutPrefix = new byte[32];
        System.arraycopy(pubKeyBytes, 12, publicKeyWithoutPrefix, 0, 32);
        return publicKeyWithoutPrefix;
    }

    public static PrivateKey privateKeyFromHexString(String privateKeyHexString) {
        byte[] privateKeyBytes = Hex.decode(privateKeyHexString);

        try {
            return getKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("PrivateKey generation failed", e);
        }
    }

    public static PublicKey publicKeyFromHexString(String publicKeyHexString) {
        byte[] publicKeyBytes = Hex.decode(publicKeyHexString);

        try {
            return getKeyFactory().generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("PrivateKey generation failed", e);
        }
    }

    public static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not get KeyFactory", e);
        }
    }

}
