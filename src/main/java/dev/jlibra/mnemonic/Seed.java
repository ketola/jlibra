package dev.jlibra.mnemonic;

import javax.annotation.concurrent.Immutable;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Immutable
public class Seed {

    private static final String MNEMONIC_SALT_PREFIX = "LIBRA WALLET: mnemonic salt prefix$";

    private final byte[] data;

    public Seed(Mnemonic mnemonic, String salt) {
        try {
            byte[] msalt = (MNEMONIC_SALT_PREFIX + salt).getBytes();

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA3-256");
            KeySpec spec = new PBEKeySpec(mnemonic.toString().toCharArray(), msalt, 2048, 256);
            Key key = factory.generateSecret(spec);

            data = key.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public byte[] getData() {
        return data.clone();
    }
}
