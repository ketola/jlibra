package dev.jlibra.admissioncontrol.query;

import static dev.jlibra.serialization.Deserialization.readBoolean;
import static dev.jlibra.serialization.Deserialization.readByteArray;
import static dev.jlibra.serialization.Deserialization.readInt;
import static dev.jlibra.serialization.Deserialization.readLong;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;
import types.AccountStateBlobOuterClass.AccountStateWithProof;

@Value.Immutable
public interface AccountResource {

    ByteArray getAuthenticationKey();

    long getBalanceInMicroLibras();

    EventHandle getReceivedEvents();

    EventHandle getSentEvents();

    int getSequenceNumber();

    boolean getDelegatedWithdrawalCapability();

    boolean getDelegatedKeyRotationCapability();

    static AccountResource deserialize(ByteSequence byteSequence) {
        try (DataInputStream accountDataStream = new DataInputStream(
                new ByteArrayInputStream(byteSequence.toArray()))) {
            int addressLength = readInt(accountDataStream, 4);
            ByteArray address = readByteArray(accountDataStream, addressLength);
            long balance = readLong(accountDataStream, 8);
            boolean delegatedKeyRotationCapability = readBoolean(accountDataStream);
            boolean delegatedWithdrawalCapability = readBoolean(accountDataStream);

            int receivedEventsCount = readInt(accountDataStream, 4);
            // skip struct attribute sequence number
            readInt(accountDataStream, 4);
            EventHandle receivedEvents = ImmutableEventHandle.builder()
                    .count(receivedEventsCount)
                    .key(readByteArray(accountDataStream, readInt(accountDataStream, 4)))
                    .build();

            int sentEventsCount = readInt(accountDataStream, 4);
            // skip struct attribute sequence number
            readInt(accountDataStream, 4);
            EventHandle sentEvents = ImmutableEventHandle.builder()
                    .key(readByteArray(accountDataStream, readInt(accountDataStream, 4)))
                    .count(sentEventsCount)
                    .build();

            return ImmutableAccountResource.builder()
                    .authenticationKey(address)
                    .sequenceNumber(readInt(accountDataStream, 4))
                    .balanceInMicroLibras(balance)
                    .delegatedWithdrawalCapability(delegatedWithdrawalCapability)
                    .delegatedKeyRotationCapability(delegatedKeyRotationCapability)
                    .receivedEvents(receivedEvents)
                    .sentEvents(sentEvents)
                    .build();
        } catch (IOException e) {
            throw new LibraRuntimeException("Deserialization of AccountResource failed", e);
        }
    }

    static List<AccountResource> fromGrpcObject(AccountStateWithProof accountStateWithProof) {
        List<AccountResource> accountResources = new ArrayList<>();

        DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(accountStateWithProof.getBlob().getBlob().toByteArray()));
        int dataSize = readInt(in, 4);

        for (int i = 0; i < dataSize; i++) {
            int keyLength = readInt(in, 4);
            ByteSequence keyValue = readByteArray(in, keyLength);
            int valLength = readInt(in, 4);
            ByteSequence val = readByteArray(in, valLength);
            accountResources.add(AccountResource.deserialize(val));
        }

        return accountResources;
    }

}
