package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import dev.jlibra.serialization.ByteSequence;

public class EventTest {

    private static ByteSequence EVENT_DATA = ByteSequence.from("40420f00000000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d");

    @Test
    public void testFromGrpcObject() {
        types.Events.Event grpcEvent = types.Events.Event.newBuilder()
                .setEventData(EVENT_DATA.toByteString())
                .setSequenceNumber(1L)
                .build();
        Event event = Event.fromGrpcObject(grpcEvent);

        assertThat(event.getAccountAddress().getByteSequence().toString(),
                is("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"));
        assertThat(event.getAmount(), is(1000000L));
        assertThat(event.getSequenceNumber(), is(1L));
    }
}
