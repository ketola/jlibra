package dev.jlibra.admissioncontrol.query;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.immutables.value.Value;

import dev.jlibra.serialization.Deserialization;

@Value.Immutable
public interface Event {

    byte[] getAccountAddress();

    long getAmount();

    byte[] getKey();

    long getSequenceNumber();

    static Event deserialize(types.Events.Event event) {
        byte[] eventData = event.getEventData().toByteArray();
        try (DataInputStream eventDataStream = new DataInputStream(new ByteArrayInputStream(eventData))) {
            long amount = Deserialization.readLong(eventDataStream, 8);
            byte[] address = Deserialization.readBytes(eventDataStream, 32);
            return ImmutableEvent.builder()
                    .accountAddress(address)
                    .amount(amount)
                    .key(event.getKey().toByteArray())
                    .sequenceNumber(event.getSequenceNumber())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
