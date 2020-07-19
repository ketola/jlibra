package dev.jlibra.transaction;

import java.security.PrivateKey;

import org.immutables.value.Value;

import dev.jlibra.Hash;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.lcs.LCS;
import dev.jlibra.serialization.lcs.LCSSerializer;

@Value.Immutable
@LCS.Structure
public interface Signature {

    public static final int SIGNATURE_LENGTH = 64;

    public static final int BITMAP_LENGTH = 4;

    @LCS.Field(0)
    ByteArray getSignature();

    public static Signature signTransaction(Transaction transaction, PrivateKey privateKey) {
        ByteArray transactionBytes = LCSSerializer.create().serialize(transaction, Transaction.class);

        byte[] signature;

        try {
            java.security.Signature sgr = java.security.Signature.getInstance("Ed25519", "BC");
            sgr.initSign(privateKey);
            sgr.update(Hash.ofInput(transactionBytes)
                    .hash(ByteArray.from("LIBRA::RawTransaction".getBytes()))
                    .toArray());
            signature = sgr.sign();
        } catch (Exception e) {
            throw new LibraRuntimeException("Signing the transaction failed", e);
        }

        return ImmutableSignature.builder()
                .signature(ByteArray.from(signature))
                .build();
    }

    /**
     * This methods adds a signature to a multig signature.
     * 
     * @param signature   the multisig signature where the signature is added
     * @param index       the index of the public key in the multig account that was
     *                    used in the signature
     * @param transaction transaction to sign
     * @param privateKey  private key to use for signature
     * @return
     */
    public static Signature addSignatureToMultiSignature(Signature signature, int index, Transaction transaction,
            PrivateKey privateKey) {
        Signature signatureToAdd = signTransaction(transaction, privateKey);

        byte[] bitmap = signature.getSignature()
                .subseq(signature.getSignature().toArray().length - BITMAP_LENGTH,
                        BITMAP_LENGTH)
                .toArray();
        bitmap = markSigningKeyToBitmap(bitmap, index);

        byte[] signatureBytes = new byte[signature.getSignature().toArray().length - BITMAP_LENGTH
                + SIGNATURE_LENGTH];

        if (signature.getSignature().toArray().length / SIGNATURE_LENGTH > 0) {
            byte[] existingSignature = signature.getSignature()
                    .subseq(0, signature.getSignature().toArray().length - BITMAP_LENGTH)
                    .toArray();
            System.arraycopy(existingSignature, 0, signatureBytes, 0, existingSignature.length);
        }

        int signatureIndex = signatureBytes.length / SIGNATURE_LENGTH - 1;
        System.arraycopy(signatureToAdd.getSignature().toArray(), 0, signatureBytes,
                signatureIndex * SIGNATURE_LENGTH,
                SIGNATURE_LENGTH);
        byte[] signatureAndBitmap = new byte[signatureBytes.length + BITMAP_LENGTH];
        System.arraycopy(signatureBytes, 0, signatureAndBitmap, 0, signatureBytes.length);
        System.arraycopy(bitmap, 0, signatureAndBitmap, signatureAndBitmap.length - BITMAP_LENGTH,
                BITMAP_LENGTH);

        return ImmutableSignature.builder()
                .signature(ByteArray.from(signatureAndBitmap))
                .build();
    }

    /**
     * bitmap returned by this method is used to mark which signatures of the
     * multisig account are present in the multisig signature. Not all of the
     * signatures are needed if the threshold is smaller than the amount of keys in
     * the account, but to be able to check the signature, the multisig signature
     * must contain the information of what keys were used.
     * 
     * example: if 1st and 5th signature are present, the returned bitmap contains
     * bits 10001000..
     */
    static byte[] markSigningKeyToBitmap(byte[] bitmap, int signatureIndex) {
        int bucket = signatureIndex / 8;
        int bucketPos = signatureIndex - (bucket * 8);
        bitmap[bucket] |= 128 >> bucketPos;
        return bitmap;
    }

    public static Signature newMultisignature() {
        // new multisignature initially only contains the bytes required for the bitmap,
        // the
        // actual signatures have to be added by using addSignatureToMultiSignature
        // method
        return ImmutableSignature.builder()
                .signature(ByteArray.from(new byte[4]))
                .build();
    }

}
