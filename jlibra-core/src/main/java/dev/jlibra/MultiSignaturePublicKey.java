package dev.jlibra;

import static dev.jlibra.PublicKey.PUBLIC_KEY_LENGTH;

import java.util.List;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

/**
 * A MultiSignaturePublicKey key is used for accounts that use several keys to
 * sign a transaction.
 */
public class MultiSignaturePublicKey implements ByteSequence {

    private ByteArray bytes;

    private MultiSignaturePublicKey(ByteArray bytes) {
        this.bytes = bytes;
    }

    /**
     * Creates a new MultiSignaturePublicKey
     * 
     * @param publicKeys List of {@link PublicKey} 's of the account
     * @param threshold  number of keys required to sign a transaction for the
     *                   account
     * @return
     */
    public static MultiSignaturePublicKey create(List<PublicKey> publicKeys, int threshold) {
        byte[] multiSigPublicKeyBytes = new byte[publicKeys.size() * PUBLIC_KEY_LENGTH + 1];
        int counter = 0;
        for (PublicKey pk : publicKeys) {
            byte[] pkBytes = pk.toArray();
            System.arraycopy(pkBytes, 0, multiSigPublicKeyBytes, counter++ * PUBLIC_KEY_LENGTH, PUBLIC_KEY_LENGTH);
        }
        multiSigPublicKeyBytes[multiSigPublicKeyBytes.length - 1] = (byte) threshold;
        return new MultiSignaturePublicKey(ByteArray.from(multiSigPublicKeyBytes));
    }

    public byte[] toArray() {
        return bytes.toArray();
    }

    @Override
    public String toString() {
        return bytes.toString();
    }
}
