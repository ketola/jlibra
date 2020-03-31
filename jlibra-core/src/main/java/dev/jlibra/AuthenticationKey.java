package dev.jlibra;

import static dev.jlibra.PublicKey.PUBLIC_KEY_LENGTH;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class AuthenticationKey implements ByteSequence {

    private static final byte SIGNATURE_SCHEME_ED25519 = 0;

    private ByteArray bytes;

    private AuthenticationKey(ByteArray bytes) {
        this.bytes = bytes;
    }

    public static AuthenticationKey fromPublicKey(PublicKey publicKey) {
        byte[] pkBytes = publicKey.toArray();
        byte[] signatureSchemeId = new byte[] { SIGNATURE_SCHEME_ED25519 };
        byte[] pkBytesWithSignatureSchemeId = new byte[PUBLIC_KEY_LENGTH + 1];

        System.arraycopy(pkBytes, 0, pkBytesWithSignatureSchemeId, 0, PUBLIC_KEY_LENGTH);
        System.arraycopy(signatureSchemeId, 0, pkBytesWithSignatureSchemeId, PUBLIC_KEY_LENGTH, 1);

        return new AuthenticationKey(Hash.ofInput(ByteArray.from(pkBytesWithSignatureSchemeId)).hash());
    }

    public static AuthenticationKey fromPublicKey(java.security.PublicKey javaPublicKey) {
        return fromPublicKey(PublicKey.fromPublicKey(javaPublicKey));
    }

    public static AuthenticationKey fromByteArray(ByteArray bytes) {
        return new AuthenticationKey(bytes);
    }

    public static AuthenticationKey fromHexString(String hexString) {
        return new AuthenticationKey(ByteArray.from(hexString));
    }

    public ByteArray toByteArray() {
        return ByteArray.from(bytes.toArray());
    }

    @Override
    public byte[] toArray() {
        return bytes.toArray();
    }

    @Override
    public ByteString toByteString() {
        return bytes.toByteString();
    }

    @Override
    public String toString() {
        return bytes.toString();
    }

}
