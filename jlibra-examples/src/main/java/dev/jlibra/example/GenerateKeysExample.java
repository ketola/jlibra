package dev.jlibra.example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.KeyUtils;

public class GenerateKeysExample {

    private static final Logger logger = LogManager.getLogger(GenerateKeysExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");

        KeyPair keyPair = kpGen.generateKeyPair();

        BCEdDSAPrivateKey privateKey = (BCEdDSAPrivateKey) keyPair.getPrivate();

        BCEdDSAPublicKey publicKey = (BCEdDSAPublicKey) keyPair.getPublic();

        logger.info("Libra address: {}", KeyUtils.toHexStringLibraAddress(publicKey.getEncoded()));
        logger.info("Public key: {}", Hex.toHexString(publicKey.getEncoded()));
        logger.info("Private key: {}", Hex.toHexString(privateKey.getEncoded()));
    }

}
