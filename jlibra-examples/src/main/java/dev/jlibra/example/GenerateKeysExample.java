package dev.jlibra.example;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AccountAddress;
import dev.jlibra.AuthenticationKey;
import dev.jlibra.serialization.ByteArray;

public class GenerateKeysExample {

    private static final Logger logger = LoggerFactory.getLogger(GenerateKeysExample.class);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("Ed25519", "BC");
        KeyPair keyPair = kpGen.generateKeyPair();

        BCEdDSAPrivateKey privateKey = (BCEdDSAPrivateKey) keyPair.getPrivate();

        BCEdDSAPublicKey publicKey = (BCEdDSAPublicKey) keyPair.getPublic();

        AuthenticationKey authenticationKey = AuthenticationKey.fromPublicKey(publicKey);
        logger.info("Libra address: {}",
                AccountAddress.fromAuthenticationKey(authenticationKey));
        logger.info("Authentication key: {}", authenticationKey);
        logger.info("Public key: {}", ByteArray.from(publicKey.getEncoded()));
        logger.info("Private key: {}", ByteArray.from(privateKey.getEncoded()));
    }

}
