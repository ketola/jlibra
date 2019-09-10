package dev.jlibra;

import static java.util.stream.Collectors.toList;

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

import dev.jlibra.admissioncontrol.query.AccountData;
import dev.jlibra.admissioncontrol.query.EventHandle;
import dev.jlibra.admissioncontrol.query.EventPath;
import dev.jlibra.admissioncontrol.query.ImmutableAccountData;
import dev.jlibra.admissioncontrol.query.ImmutableEventHandle;
import dev.jlibra.admissioncontrol.query.ImmutableEventPath;
import dev.jlibra.admissioncontrol.query.ImmutablePaymentEvent;
import dev.jlibra.admissioncontrol.query.ImmutableSignedTransactionWithProof;
import dev.jlibra.admissioncontrol.query.PaymentEvent;
import dev.jlibra.admissioncontrol.query.SignedTransactionWithProof;
import types.Events.Event;
import types.GetWithProof.GetAccountStateResponse;
import types.GetWithProof.GetAccountTransactionBySequenceNumberResponse;
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

    public static List<AccountData> readAccountStates(GetAccountStateResponse getAccountStateResponse) {
        List<AccountData> accountStates = new ArrayList<>();

        byte[] blobBytes = getAccountStateResponse.getAccountStateWithProof().getBlob().getBlob().toByteArray();

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
            boolean delegatedWithdrawalCapability = readBoolean(stateStream);

            int receivedEventsCount = readInt(stateStream, 4);
            // skip struct attribute sequence number
            readInt(stateStream, 4);
            int receivedKeyLength = readInt(stateStream, 4);
            byte[] eventKey = readBytes(stateStream, addressLength);
            EventHandle receivedEvents = ImmutableEventHandle.builder()
                    .key(eventKey)
                    .count(receivedEventsCount)
                    .build();

            int sentEventsCount = readInt(stateStream, 4);
            // skip struct attribute sequence number
            readInt(stateStream, 4);
            int sentKeyLength = readInt(stateStream, 4);
            byte[] eventKey2 = readBytes(stateStream, addressLength);
            EventHandle sentEvents = ImmutableEventHandle.builder()
                    .key(eventKey2)
                    .count(sentEventsCount)
                    .build();

            int sequenceNumber = readInt(stateStream, 4);

            accountStates.add(ImmutableAccountData.builder()
                    .accountAddress(address)
                    .sequenceNumber(sequenceNumber)
                    .balanceInMicroLibras(balance)
                    .delegatedWithdrawalCapability(delegatedWithdrawalCapability)
                    .receivedEvents(receivedEvents)
                    .sentEvents(sentEvents).build());
        });

        return accountStates;
    }

    public static SignedTransactionWithProof readSignedTransactionWithProof(
            GetAccountTransactionBySequenceNumberResponse getAccountTransactionBySequenceNumberResponse) {

        byte[] senderPublicKey = getAccountTransactionBySequenceNumberResponse.getSignedTransactionWithProof()
                .getSignedTransaction().getSenderPublicKey()
                .toByteArray();
        byte[] senderSignature = getAccountTransactionBySequenceNumberResponse.getSignedTransactionWithProof()
                .getSignedTransaction().getSenderSignature()
                .toByteArray();
        List<PaymentEvent> events = getAccountTransactionBySequenceNumberResponse.getSignedTransactionWithProof()
                .getEvents().getEventsList().stream()
                .map(LibraHelper::readPaymentEvent).collect(toList());

        return ImmutableSignedTransactionWithProof.builder()
                .addAllEvents(events)
                .senderPublicKey(senderPublicKey)
                .senderSignature(senderSignature)
                .build();
    }

    public static PaymentEvent readPaymentEvent(Event event) {
        byte[] pathBytes = event.getAccessPath().getPath().toByteArray();
        DataInputStream pathStream = new DataInputStream(new ByteArrayInputStream(pathBytes));
        byte[] tag = readBytes(pathStream, 1);
        byte[] path = readBytes(pathStream, 32);
        byte[] suffixBytes = readBytes(pathStream, pathBytes.length - 33);
        EventPath eventPath = ImmutableEventPath.builder()
                .tag(tag[0])
                .accountResourcePath(path)
                .suffix(new String(suffixBytes))
                .build();

        byte[] eventBytes = event.getEventData().toByteArray();
        DataInputStream eventStream = new DataInputStream(new ByteArrayInputStream(eventBytes));
        long balance = readInt(eventStream, 8);
        int addressLength = readInt(eventStream, 4);
        byte[] address = readBytes(eventStream, addressLength);
        return ImmutablePaymentEvent.builder()
                .address(address)
                .amount(balance)
                .eventPath(eventPath)
                .build();
    }

    private static int readInt(DataInputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static long readLong(DataInputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    private static boolean readBoolean(DataInputStream in) {
        byte[] data = readBytes(in, 1);
        return data[0] == 1;
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
