package dev.jlibra;

import static dev.jlibra.PublicKey.PUBLIC_KEY_LENGTH;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;

public class AuthenticatioKeyPreimage implements ByteSequence {

    private ByteArray bytes;

    private AuthenticatioKeyPreimage(ByteArray bytes) {
        this.bytes = bytes;
    }

    public static AuthenticatioKeyPreimage fromPublicKey(PublicKey publicKey) {
        byte[] pkBytes = publicKey.toArray();
        byte[] signatureSchemeId = Serializer.builder().appendInt(0).toByteArray().toArray();
        byte[] pkBytesWithSignatureSchemeId = new byte[PUBLIC_KEY_LENGTH + Integer.BYTES];

        System.arraycopy(pkBytes, 0, pkBytesWithSignatureSchemeId, 0, PUBLIC_KEY_LENGTH);
        System.arraycopy(signatureSchemeId, 0, pkBytesWithSignatureSchemeId, PUBLIC_KEY_LENGTH, Integer.BYTES);

        return new AuthenticatioKeyPreimage(Hash.ofInput(ByteArray.from(pkBytesWithSignatureSchemeId)).hash());
    }

    public static AuthenticatioKeyPreimage fromPublicKey(java.security.PublicKey javaPublicKey) {
        return fromPublicKey(PublicKey.fromPublicKey(javaPublicKey));
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
        return ByteString.copyFrom(bytes.toArray());
    }

    @Override
    public String toString() {
        return bytes.toString();
    }
}
