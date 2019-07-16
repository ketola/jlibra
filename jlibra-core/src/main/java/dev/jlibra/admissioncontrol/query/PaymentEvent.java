package dev.jlibra.admissioncontrol.query;

import static dev.jlibra.admissioncontrol.query.PaymentEvent.EventPath.EventType.RECEIVE_LIBRA;
import static dev.jlibra.admissioncontrol.query.PaymentEvent.EventPath.EventType.SEND_LIBRA;

public class PaymentEvent {

    private byte[] address;
    private long amount;
    private EventPath eventPath;

    public PaymentEvent(byte[] address, long amount, EventPath eventPath) {
        this.address = address;
        this.amount = amount;
        this.eventPath = eventPath;
    }

    public byte[] getAddress() {
        return address;
    }

    public long getAmount() {
        return amount;
    }

    public EventPath getEventPath() {
        return eventPath;
    }

    public static class EventPath {

        public enum EventType {
            SEND_LIBRA, RECEIVE_LIBRA
        }

        public static final byte TAG_CODE = 0;
        public static final byte TAG_RESOURCE = 1;

        public static final String SUFFIX_SENT = "/sent_events_count/";
        public static final String SUFFIX_RECEIVED = "/received_events_count/";

        private byte tag;
        private byte[] accountResourcePath;
        private String suffix;

        private EventType eventType;

        public EventPath(byte tag, byte[] accountResourcePath, String suffix) {
            this.tag = tag;
            this.accountResourcePath = accountResourcePath;
            this.suffix = suffix;
            this.eventType = suffix.equals(SUFFIX_SENT) ? SEND_LIBRA : RECEIVE_LIBRA;
        }

        public EventType getEventType() {
            return this.eventType;
        }

        public byte getTag() {
            return tag;
        }

        public byte[] getAccountResourcePath() {
            return accountResourcePath;
        }

        public void setAccountResourcePath(byte[] accountResourcePath) {
            this.accountResourcePath = accountResourcePath;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }

}
