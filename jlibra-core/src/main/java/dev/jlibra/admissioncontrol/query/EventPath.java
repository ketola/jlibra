package dev.jlibra.admissioncontrol.query;

import static dev.jlibra.admissioncontrol.query.EventPath.EventType.RECEIVE_LIBRA;
import static dev.jlibra.admissioncontrol.query.EventPath.EventType.SEND_LIBRA;

import org.immutables.value.Value;

@Value.Immutable
public interface EventPath {
    enum EventType {
        SEND_LIBRA, RECEIVE_LIBRA
    }

    byte TAG_CODE = 0;
    byte TAG_RESOURCE = 1;

    String SUFFIX_SENT = "/sent_events_count/";
    String SUFFIX_RECEIVED = "/received_events_count/";

    default EventType getEventType() {
        return getSuffix().equals(SUFFIX_SENT) ? SEND_LIBRA : RECEIVE_LIBRA;
    }

    byte getTag();

    byte[] getAccountResourcePath();

    String getSuffix();
}
