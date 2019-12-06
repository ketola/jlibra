package dev.jlibra.admissioncontrol.query;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

import com.google.protobuf.ByteString;

public class EventTest {

    private static String EVENT_DATA = "40420f00000000008f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d";

    @Test
    public void testFromGrpcObject() {
        types.Events.Event grpcEvent = types.Events.Event.newBuilder()
                .setEventData(ByteString.copyFrom(Hex.decode(EVENT_DATA)))
                .setSequenceNumber(1L)
                .build();
        Event event = Event.fromGrpcObject(grpcEvent);

        assertThat(event.getAccountAddress().asHexString(),
                is("8f5fbb9486acc5fb90f1a6be43a0013d4a7f7f06e3d5fe995be1e9b272c09b5d"));
        assertThat(event.getAmount(), is(1000000L));
        assertThat(event.getSequenceNumber(), is(1L));
    }
}
