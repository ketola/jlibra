package dev.jlibra.example;

import java.security.KeyPair;
import java.security.Security;

import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.LibraHelper;

public class GenerateKeysExample {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();

        java.security.KeyPairGenerator kpGen = java.security.KeyPairGenerator.getInstance("Ed25519", "BC");

        KeyPair keyPair = kpGen.generateKeyPair();

        BCEdDSAPrivateKey privateKey = (BCEdDSAPrivateKey) keyPair.getPrivate();

        BCEdDSAPublicKey publicKey = (BCEdDSAPublicKey) keyPair.getPublic();

        System.out.println(
                "Libra address: " + new String(Hex.encode(digestSHA3.digest(LibraHelper.stripPrefix(publicKey)))));
        System.out.println("Public key: " + new String(Hex.encode(publicKey.getEncoded())));
        System.out.println("Private key: " + new String(Hex.encode(privateKey.getEncoded())));
    }

}
