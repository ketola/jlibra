package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import dev.jlibra.serialization.ByteArray;

public class EventTest {

    private static ByteArray EVENT_DATA = ByteArray
            .from("40420f00000000000990455b46e5eee5145e4de4be27ce7300000000");

    @Test
    public void testFromGrpcObjectWithEmptyMetadata() {
        types.Events.Event grpcEvent = types.Events.Event.newBuilder()
                .setEventData(EVENT_DATA.toByteString())
                .setSequenceNumber(1L)
                .build();
        Event event = Event.fromGrpcObject(grpcEvent);

        assertThat(event.getAccountAddress().toString(),
                is("0990455b46e5eee5145e4de4be27ce73"));
        assertThat(event.getAmount(), is(1000000L));
        assertThat(event.getSequenceNumber(), is(1L));
        assertFalse(event.getMetadata().isPresent());
    }
}
