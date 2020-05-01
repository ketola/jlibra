package dev.jlibra.mnemonic;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.KeyUtils;
import dev.jlibra.LibraRuntimeException;

public class ExtendedPrivKey {

    public final PrivateKey privateKey;
    public final PublicKey publicKey;

    public ExtendedPrivKey(SecretKey secretKey) {
        Ed25519PrivateKeyParameters pKeyParams = new Ed25519PrivateKeyParameters(secretKey.getByteSequence().toArray(),
                0);

        try {
            PrivateKeyInfo keyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(pKeyParams);
            this.privateKey = KeyUtils.getKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(keyInfo.getEncoded()));
            this.publicKey = BouncyCastleProvider.getPublicKey(new SubjectPublicKeyInfo(
                    new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519), keyInfo.getPublicKeyData().getBytes()));
        } catch (IOException | InvalidKeySpecException e) {
            throw new LibraRuntimeException("Key creation failed", e);
        }
    }

    public AccountAddress getAddress() {
        return AccountAddress.fromAuthenticationKey(AuthenticationKey.fromPublicKey(publicKey));
    }
}
