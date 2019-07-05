package dev.jlibra;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import types.AccountStateBlobOuterClass.AccountStateWithProof;
import types.Transaction.RawTransaction;

public class LibraHelper {

    private static final byte[] ACCOUNT_STATE_PATH = Hex
            .decode("01217da6c6b3e19f1825cfb2676daecce3bf3de03cf26647c78df00b371b25cc97");

    public static byte[] signTransaction(RawTransaction rawTransaction, PrivateKey privateKey) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] saltDigest = digestSHA3.digest("RawTransaction@@$$LIBRA$$@@".getBytes());
        byte[] transactionBytes = rawTransaction.toByteArray();
        byte[] saltDigestAndTransaction = new byte[saltDigest.length + transactionBytes.length];

        System.arraycopy(saltDigest, 0, saltDigestAndTransaction, 0, saltDigest.length);
        System.arraycopy(transactionBytes, 0, saltDigestAndTransaction, saltDigest.length, transactionBytes.length);

        byte[] signature;

        try {
            Signature sgr = Signature.getInstance("Ed25519", "BC");
            sgr.initSign(privateKey);
            sgr.update(digestSHA3.digest(saltDigestAndTransaction));
            signature = sgr.sign();
        } catch (Exception e) {
            throw new RuntimeException("Signing the transaction failed", e);
        }

        return signature;
    }

    public static String toLibraAddress(PublicKey publicKey) {
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        return new String(Hex.encode(digestSHA3.digest(stripPrefix(publicKey))));
    }

    public static byte[] stripPrefix(PublicKey publicKey) {
        return stripPrefix(publicKey.getEncoded());
    }

    public static byte[] stripPrefix(byte[] pubKeyBytes) {
        byte[] publicKeyWithoutPrefix = new byte[32];
        System.arraycopy(pubKeyBytes, 12, publicKeyWithoutPrefix, 0, 32);
        return publicKeyWithoutPrefix;
    }

    public static PrivateKey privateKeyFromHexString(String privateKeyHexString) {
        byte[] privateKeyBytes = Hex.decode(privateKeyHexString);

        try {
            return getKeyFactory().generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("PrivateKey generation failed", e);
        }
    }

    public static PublicKey publicKeyFromHexString(String publicKeyHexString) {
        byte[] publicKeyBytes = Hex.decode(publicKeyHexString);

        try {
            return getKeyFactory().generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("PrivateKey generation failed", e);
        }
    }

    private static KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not get KeyFactory", e);
        }
    }

    public static byte[] transferMoveScript() {
        try {
            return IOUtils.toByteArray(LibraHelper.class.getResourceAsStream("/move/transfer.bin"));
        } catch (Exception e) {
            throw new RuntimeException("Reading the transfer script file failed", e);
        }
    }

    public static List<AccountState> readAccountStates(AccountStateWithProof accountStateWithProof) {
        List<AccountState> accountStates = new ArrayList<>();

        byte[] blobBytes = accountStateWithProof.getBlob().getBlob().toByteArray();

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(blobBytes));
        int dataSize = readInt(in, 4);

        Set<byte[]> states = new LinkedHashSet<>();

        for (int i = 0; i < dataSize; i++) {
            int keyLength = readInt(in, 4);
            byte[] key = readBytes(in, keyLength);
            int valLength = readInt(in, 4);
            byte[] val = readBytes(in, valLength);

            if (Arrays.equals(ACCOUNT_STATE_PATH, key)) {
                states.add(val);
            }
        }

        states.forEach(state -> {
            DataInputStream stateStream = new DataInputStream(new ByteArrayInputStream(state));
            int addressLength = readInt(stateStream, 4);
            byte[] address = readBytes(stateStream, addressLength);
            int balance = readInt(stateStream, 8);
            int receivedEvents = readInt(stateStream, 8);
            int sentEvents = readInt(stateStream, 8);
            int sequenceNumber = readInt(stateStream, 8);

            accountStates.add(new AccountState(address, balance, receivedEvents, sentEvents, sequenceNumber));
        });

        return accountStates;
    }

    private static int readInt(DataInputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static byte[] readBytes(DataInputStream in, int len) {
        byte[] data = new byte[len];
        try {
            in.read(data);
        } catch (IOException e) {
            throw new RuntimeException("Could not read integer from stream", e);
        }
        return data;
    }
}
