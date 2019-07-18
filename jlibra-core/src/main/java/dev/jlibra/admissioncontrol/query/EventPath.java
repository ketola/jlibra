package dev.jlibra.admissioncontrol.query;

import static dev.jlibra.admissioncontrol.query.EventPath.EventType.RECEIVE_LIBRA;
import static dev.jlibra.admissioncontrol.query.EventPath.EventType.SEND_LIBRA;

import org.immutables.value.Value;

@Value.Immutable
public abstract class EventPath {
    public enum EventType {
        SEND_LIBRA, RECEIVE_LIBRA
    }

    public static final byte TAG_CODE = 0;
    public static final byte TAG_RESOURCE = 1;

    public static final String SUFFIX_SENT = "/sent_events_count/";
    public static final String SUFFIX_RECEIVED = "/received_events_count/";

    public EventType getEventType() {
        return getSuffix().equals(SUFFIX_SENT) ? SEND_LIBRA : RECEIVE_LIBRA;
    }

    abstract byte getTag();

    abstract byte[] getAccountResourcePath();

    abstract String getSuffix();
}
