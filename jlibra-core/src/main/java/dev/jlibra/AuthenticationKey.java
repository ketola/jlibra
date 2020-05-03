package dev.jlibra;

import static dev.jlibra.PublicKey.PUBLIC_KEY_LENGTH;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class AuthenticationKey implements ByteSequence {

    private static final byte SIGNATURE_SCHEME_ED25519 = 0;

    private static final byte SIGNATURE_SCHEME_MULTI_ED25519 = 1;

    private ByteArray bytes;

    private AuthenticationKey(ByteArray bytes) {
        this.bytes = bytes;
    }

    public static AuthenticationKey fromPublicKey(PublicKey publicKey) {
        byte[] pkBytes = publicKey.toArray();
        byte[] pkBytesWithSignatureSchemeId = new byte[PUBLIC_KEY_LENGTH + 1];

        System.arraycopy(pkBytes, 0, pkBytesWithSignatureSchemeId, 0, PUBLIC_KEY_LENGTH);
        pkBytesWithSignatureSchemeId[PUBLIC_KEY_LENGTH] = SIGNATURE_SCHEME_ED25519;

        return new AuthenticationKey(Hash.ofInput(ByteArray.from(pkBytesWithSignatureSchemeId)).hash());
    }

    public static AuthenticationKey fromMultiSignaturePublicKey(MultiSignaturePublicKey publicKey) {
        byte[] pkBytes = publicKey.toArray();
        byte[] pkBytesWithSignatureSchemeId = new byte[pkBytes.length + 1];

        System.arraycopy(pkBytes, 0, pkBytesWithSignatureSchemeId, 0, pkBytes.length);
        pkBytesWithSignatureSchemeId[pkBytes.length] = SIGNATURE_SCHEME_MULTI_ED25519;

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

    public ByteArray prefix() {
        return bytes.subseq(0, 16);
    }

    @Override
    public byte[] toArray() {
        return bytes.toArray();
    }

    @Override
    public String toString() {
        return bytes.toString();
    }

}
