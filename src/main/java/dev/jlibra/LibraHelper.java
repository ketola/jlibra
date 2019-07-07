package dev.jlibra;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import dev.jlibra.PaymentEvent.EventPath;
import types.AccountStateBlobOuterClass.AccountStateWithProof;
import types.Events.Event;
import types.Transaction.RawTransaction;

public class LibraHelper {

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

            states.add(val);
        }

        states.forEach(state -> {
            DataInputStream stateStream = new DataInputStream(new ByteArrayInputStream(state));
            int addressLength = readInt(stateStream, 4);
            byte[] address = readBytes(stateStream, addressLength);
            long balance = readLong(stateStream, 8);
            long receivedEvents = readLong(stateStream, 8);
            long sentEvents = readLong(stateStream, 8);
            long sequenceNumber = readLong(stateStream, 8);

            accountStates.add(new AccountState(address, balance, receivedEvents, sentEvents, sequenceNumber));
        });

        return accountStates;
    }

    public static PaymentEvent readPaymentEvent(Event event) {
        byte[] pathBytes = event.getAccessPath().getPath().toByteArray();
        DataInputStream pathStream = new DataInputStream(new ByteArrayInputStream(pathBytes));
        byte[] tag = readBytes(pathStream, 1);
        byte[] path = readBytes(pathStream, 32);
        byte[] suffixBytes = readBytes(pathStream, pathBytes.length - 32);
        EventPath eventPath = new EventPath(tag[0], path, new String(suffixBytes));

        byte[] eventBytes = event.getEventData().toByteArray();
        DataInputStream eventStream = new DataInputStream(new ByteArrayInputStream(eventBytes));
        long balance = readInt(eventStream, 8);
        int addressLength = readInt(eventStream, 4);
        byte[] address = readBytes(eventStream, addressLength);
        return new PaymentEvent(address, balance, eventPath);
    }

    private static int readInt(DataInputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static long readLong(DataInputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    private static byte[] readBytes(DataInputStream in, int len) {
        byte[] data = new byte[len];
        try {
            in.read(data);
        } catch (IOException e) {
            throw new RuntimeException("Could not read input stream", e);
        }
        return data;
    }
}
