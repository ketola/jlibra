package dev.jlibra;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import dev.jlibra.serialization.ByteSequence;

public class KeyUtils {

    public static ByteSequence toByteSequenceLibraAddress(ByteSequence publicKeyBytes) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        return ByteSequence
                .from(digestSHA3
                .digest(stripPublicKeyPrefix(publicKeyBytes).toArray()));
    }

    public static ByteSequence stripPublicKeyPrefix(ByteSequence pubKeyBytes) {
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
