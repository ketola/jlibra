package dev.jlibra.admissioncontrol.query;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.stream.IntStream;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Deserialization;

@Value.Immutable
public interface Event {

    AccountAddress getAccountAddress();

    long getAmount();

    ByteSequence getKey();

    long getSequenceNumber();

    ByteSequence getMetadata();

    static Event fromGrpcObject(types.Events.Event event) {
        byte[] eventData = event.getEventData().toByteArray();
        try (DataInputStream eventDataStream = new DataInputStream(new ByteArrayInputStream(eventData))) {
            long amount = Deserialization.readLong(eventDataStream, 8);
            ByteSequence address = Deserialization.readByteSequence(eventDataStream, 32);
            ByteSequence message = ByteSequence.from(new byte[0]);
            if (eventDataStream.available() > 0) {
                ByteSequence messageData = Deserialization.readByteSequence(eventDataStream, eventDataStream.available());
                boolean onlyZerosStream = IntStream.range(0, messageData.toArray().length).allMatch(i -> messageData.toArray()[i] == 0);
                if (!onlyZerosStream) {
                    message = messageData;
                }
            }
            return ImmutableEvent.builder()
                    .accountAddress(AccountAddress.ofByteSequence(address))
                    .amount(amount)
                    .key(ByteSequence.from(event.getKey().toByteArray()))
                    .metadata(message)
                    .key(ByteSequence.from(event.getKey().toByteArray()))
                    .sequenceNumber(event.getSequenceNumber())
                    .build();
        } catch (IOException e) {
            throw new LibraRuntimeException("Converting from grpc object failed", e);
        }

    }
}
