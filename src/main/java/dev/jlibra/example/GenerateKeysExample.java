package dev.jlibra.example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.KeyUtils;

public class GenerateKeysExample {

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");

        KeyPair keyPair = kpGen.generateKeyPair();

        BCEdDSAPrivateKey privateKey = (BCEdDSAPrivateKey) keyPair.getPrivate();

        BCEdDSAPublicKey publicKey = (BCEdDSAPublicKey) keyPair.getPublic();

        System.out.println(
                "Libra address: "
                        + KeyUtils.toHexStringLibraAddress(publicKey.getEncoded()));
        System.out.println("Public key: " + Hex.toHexString(publicKey.getEncoded()));
        System.out.println("Private key: " + Hex.toHexString(privateKey.getEncoded()));
    }

}
