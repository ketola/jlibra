package dev.jlibra.admissioncontrol.query;

import static dev.jlibra.serialization.Deserialization.readBoolean;
import static dev.jlibra.serialization.Deserialization.readByteArray;
import static dev.jlibra.serialization.Deserialization.readInt;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.immutables.value.Value;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Deserialization;
import types.AccountStateBlobOuterClass.AccountStateWithProof;

@Value.Immutable
public interface AccountResource {

    ByteSequence getAuthenticationKey();

    long getBalanceInMicroLibras();

    EventHandle getReceivedEvents();

    EventHandle getSentEvents();

    int getSequenceNumber();

    boolean getDelegatedWithdrawalCapability();

    boolean getDelegatedKeyRotationCapability();

    static AccountResource deserialize(ByteSequence byteSequence) {
        try (DataInputStream accountDataStream = new DataInputStream(
                new ByteArrayInputStream(byteSequence.toArray()))) {

            // The AccountResource / BalanceResource are in map
            int numberOfItems = readInt(accountDataStream, 4);
            int keyLength = readInt(accountDataStream, 4);
            ByteSequence keyValue = readByteArray(accountDataStream, keyLength);
            int valLength = readInt(accountDataStream, 4);

            int authKeyLength = readInt(accountDataStream, 4);
            ByteSequence authKey = readByteArray(accountDataStream, authKeyLength);

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
            long sequenceNumber = Deserialization.readLong(accountDataStream, 8);
            long eventGenerator = Deserialization.readLong(accountDataStream, 8);

            // next item in map, the balance resource
            keyLength = readInt(accountDataStream, 4);
            keyValue = readByteArray(accountDataStream, keyLength);
            valLength = readInt(accountDataStream, 4);
            long balance = Deserialization.readLong(accountDataStream, 8);

            return ImmutableAccountResource.builder()
                    .authenticationKey(authKey)
                    .sequenceNumber((int) sequenceNumber)
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

    static AccountResource fromGrpcObject(AccountStateWithProof accountStateWithProof) {
        byte[] byteArray = accountStateWithProof.getBlob().getBlob().toByteArray();

        ByteArray from = ByteArray.from(byteArray);
        System.out.println(from);
        return AccountResource.deserialize(from);
    }

}
