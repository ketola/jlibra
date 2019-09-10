package dev.jlibra;

import static dev.jlibra.serialization.Deserialization.readBytes;
import static dev.jlibra.serialization.Deserialization.readInt;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bouncycastle.jcajce.provider.digest.SHA3;

import dev.jlibra.admissioncontrol.query.AccountData;
import dev.jlibra.admissioncontrol.query.Event;
import dev.jlibra.admissioncontrol.query.ImmutableSignedTransactionWithProof;
import dev.jlibra.admissioncontrol.query.SignedTransactionWithProof;
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
            accountStates.add(AccountData.deserialize(state));
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
        List<Event> events = getAccountTransactionBySequenceNumberResponse.getSignedTransactionWithProof()
                .getEvents().getEventsList().stream()
                .map(Event::deserialize).collect(toList());

        return ImmutableSignedTransactionWithProof.builder()
                .addAllEvents(events)
                .senderPublicKey(senderPublicKey)
                .senderSignature(senderSignature)
                .build();
    }

}
