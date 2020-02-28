package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import dev.jlibra.serialization.ByteArray;

public class EventTest {

    private static ByteArray EVENT_DATA = ByteArray
            .from("40420f00000000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d00000000");
    private static ByteArray EVENT_DATA_WITH_METADATA = ByteArray
            .from("40420f00000000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d0500000068656c6c6f");

    @Test
    public void testFromGrpcObject() {
        types.Events.Event grpcEvent = types.Events.Event.newBuilder()
                .setEventData(EVENT_DATA.toByteString())
                .setSequenceNumber(1L)
                .build();
        Event event = Event.fromGrpcObject(grpcEvent);

        assertThat(event.getAccountAddress().toString(),
                is("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"));
        assertThat(event.getAmount(), is(1000000L));
        assertThat(event.getSequenceNumber(), is(1L));
    }

    @Test
    public void testFromGrpcObjectWithEmptyMetadata() {
        types.Events.Event grpcEvent = types.Events.Event.newBuilder()
                .setEventData(EVENT_DATA.toByteString())
                .setSequenceNumber(1L)
                .build();
        Event event = Event.fromGrpcObject(grpcEvent);

        assertThat(event.getAccountAddress().toString(),
                is("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"));
        assertThat(event.getAmount(), is(1000000L));
        assertThat(event.getSequenceNumber(), is(1L));
        assertFalse(event.getMetadata().isPresent());
    }

    @Test
    public void testFromGrpcObjectWithAvailableMetadata() {
        types.Events.Event grpcEvent = types.Events.Event.newBuilder()
                .setEventData(EVENT_DATA_WITH_METADATA.toByteString())
                .setSequenceNumber(1L)
                .build();
        Event event = Event.fromGrpcObject(grpcEvent);

        assertThat(event.getAccountAddress().toString(),
                is("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"));
        assertThat(event.getAmount(), is(1000000L));
        assertThat(event.getSequenceNumber(), is(1L));
        assertThat(event.getMetadata().get(), is(ByteArray.from("hello".getBytes(StandardCharsets.UTF_8))));
    }
}
