package dev.jlibra;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import types.Transaction.RawTransaction;

public class LibraHelper {

    public static byte[] signTransaction(RawTransaction rawTransaction, PrivateKey privateKey) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] saltDigest = digestSHA3.digest("RawTransaction@@$$LIBRA$$@@".getBytes());
        byte[] transactionBytes = rawTransaction.toByteArray();
        byte[] saltDigestAndTransaction = new byte[saltDigest.length + transactionBytes.length];

        System.arraycopy(saltDigest, 0, saltDigestAndTransaction, 0, saltDigest.length);
        System.arraycopy(transactionBytes, 0, saltDigestAndTransaction, saltDigest.length, transactionBytes.length);

        byte[] signature;

        try {
            Signature sgr = Signature.getInstance("Ed25519", "BC");
            sgr.initSign(privateKey);
            sgr.update(digestSHA3.digest(saltDigestAndTransaction));
            signature = sgr.sign();
        } catch (Exception e) {
            throw new RuntimeException("Signing the transaction failed", e);
        }

        return signature;
    }

    public static String toLibraAddress(PublicKey publicKey) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        return new String(Hex.encode(digestSHA3.digest(stripPrefix(publicKey))));
    }

    public static byte[] stripPrefix(PublicKey publicKey) {
        return stripPrefix(publicKey.getEncoded());
    }

    public static byte[] stripPrefix(byte[] pubKeyBytes) {
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

    private static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not get KeyFactory", e);
        }
    }

    public static byte[] transferMoveScript() {
        try {
            return IOUtils.toByteArray(LibraHelper.class.getResourceAsStream("/move/transfer.bin"));
        } catch (Exception e) {
            throw new RuntimeException("Reading the transfer script file failed", e);
        }
    }
}
