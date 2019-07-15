package dev.jlibra.mnemonic;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import types.Transaction.RawTransaction;

import javax.annotation.concurrent.Immutable;
import java.nio.ByteBuffer;

@Immutable
public class ExtendedPrivKey {

    public final SecretKey privateKey;
    public final SecretKey publicKey;

    public ExtendedPrivKey(SecretKey secretKey) {
        this.privateKey = secretKey;
        this.publicKey = new SecretKey(createPublic(secretKey));
    }

    private static byte[] createPublic(SecretKey secretKey) {
        return new Ed25519PrivateKeyParameters(secretKey.getData(), 0)
                .generatePublicKey()
                .getEncoded();
    }

    public String getAddress() {
        SHA3.Digest256 digest256 = new SHA3.Digest256();
        digest256.update(publicKey.getData());
        return Hex.toHexString(digest256.digest());
    }

    public byte[] signTransaction(RawTransaction rawTransaction) {
        return sign("RawTransaction@@$$LIBRA$$@@", rawTransaction.toByteArray());
    }

    public byte[] signMessage(byte[] rawMessage) {
        return sign("Message@@$$LIBRA$$@@", rawMessage);
    }

    private byte[] sign(String prefix, byte[] bytesToSign) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] message = prepareMessage(prefix, bytesToSign);
        try {
            Ed25519Signer signer = new Ed25519Signer();
            signer.init(true, new Ed25519PrivateKeyParameters(privateKey.getData(), 0));
            signer.update(digestSHA3.digest(message), 0, 32);
            return signer.generateSignature();
        } catch (Exception e) {
            throw new RuntimeException("Signing the payload failed", e);
        }
    }

    private byte[] prepareMessage(String prefix, byte[] bytesToSign) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] saltDigest = digestSHA3.digest(prefix.getBytes());
        return ByteBuffer.allocate(saltDigest.length + bytesToSign.length)
                .put(saltDigest)
                .put(bytesToSign)
                .array();
    }

    public boolean verifyMessage(byte[] bytesMessage, byte[] signature) {
        return verify("Message@@$$LIBRA$$@@", bytesMessage, signature);
    }

    private boolean verify(String prefix, byte[] bytesToSign, byte[] signedBytes) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] message = prepareMessage(prefix, bytesToSign);
        try {
            Ed25519Signer signer = new Ed25519Signer();
            signer.init(false, new Ed25519PublicKeyParameters(publicKey.getData(), 0));
            signer.update(digestSHA3.digest(message), 0, 32);
            return signer.verifySignature(signedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Verification of payload failed", e);
        }
    }
}
