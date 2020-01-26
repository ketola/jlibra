package dev.jlibra.example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.ByteArray;

public class GenerateKeysExample {

    private static final Logger logger = LogManager.getLogger(GenerateKeysExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");

        KeyPair keyPair = kpGen.generateKeyPair();

        BCEdDSAPrivateKey privateKey = (BCEdDSAPrivateKey) keyPair.getPrivate();

        BCEdDSAPublicKey publicKey = (BCEdDSAPublicKey) keyPair.getPublic();

        logger.info("Libra address: {}", AccountAddress.fromPublicKey(publicKey));
        logger.info("Public key: {}", ByteArray.from(publicKey.getEncoded()));
        logger.info("Private key: {}", ByteArray.from(privateKey.getEncoded()));
    }

}
