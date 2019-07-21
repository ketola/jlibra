package dev.jlibra.mnemonic;

import com.google.common.annotations.VisibleForTesting;
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

    public byte[] sign(RawTransaction rawTransaction) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] saltDigest = digestSHA3.digest("RawTransaction@@$$LIBRA$$@@".getBytes());
        byte[] transactionBytes = rawTransaction.toByteArray();
        byte[] messageBytes = ByteBuffer.allocate(saltDigest.length + transactionBytes.length)
                .put(saltDigest)
                .put(transactionBytes)
                .array();
        byte[] message = digestSHA3.digest(messageBytes);

        return sign(message);
    }

    @VisibleForTesting
    byte[] sign(byte[] message) {
        try {
            Ed25519Signer signer = new Ed25519Signer();
            signer.init(true, new Ed25519PrivateKeyParameters(privateKey.getData(), 0));
            signer.update(message, 0, message.length);
            return signer.generateSignature();
        } catch (Exception e) {
            throw new RuntimeException("Signing the transaction failed", e);
        }
    }

    public boolean verify(byte[] signature, byte[] message) {
        try {
            Ed25519Signer signer = new Ed25519Signer();
            signer.init(false, new Ed25519PublicKeyParameters(publicKey.getData(), 0));
            signer.update(message, 0, message.length);
            return signer.verifySignature(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
