package dev.jlibra.admissioncontrol.query;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Optional;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.admissioncontrol.query.ImmutableEvent.Builder;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Deserialization;

@Value.Immutable
public interface Event {

    AccountAddress getAccountAddress();

    long getAmount();

    ByteSequence getKey();

    long getSequenceNumber();

    Optional<ByteSequence> getMetadata();

    static Event fromGrpcObject(types.Events.Event event) {
        byte[] eventData = event.getEventData().toByteArray();
        System.out.println(ByteArray.from(eventData));
        try (DataInputStream eventDataStream = new DataInputStream(new ByteArrayInputStream(eventData))) {
            long amount = Deserialization.readLong(eventDataStream, 8);
            ByteArray address = Deserialization.readByteArray(eventDataStream, 16);

            Builder builder = ImmutableEvent.builder()
                    .accountAddress(AccountAddress.fromByteArray(address))
                    .key(ByteArray.from(event.getKey().toByteArray()))
                    .amount(amount)
                    .sequenceNumber(event.getSequenceNumber());

            int metadataLength = Deserialization.readInt(eventDataStream, 4);
            if (metadataLength > 0) {
                builder.metadata(Deserialization.readByteArray(eventDataStream, metadataLength));
            }

            return builder
                    .build();
        } catch (IOException e) {
            throw new LibraRuntimeException("Converting from grpc object failed", e);
        }

    }
}
